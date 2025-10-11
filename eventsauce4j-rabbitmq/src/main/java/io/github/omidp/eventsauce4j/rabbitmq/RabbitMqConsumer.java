/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.omidp.eventsauce4j.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqConsumer {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqConsumer.class);

	private final Inflector inflector;
	private final List<MessageConsumer> messageConsumers;
	private final RabbitMqConfiguration rabbitMqConfiguration;
	private final RabbitMqSetup rabbitMqSetup;
	private final EventPublicationRepository eventPublicationRepository;

	public RabbitMqConsumer(Inflector inflector, List<MessageConsumer> messageConsumers,
							RabbitMqConfiguration rabbitMqConfiguration,
							RabbitMqSetup rabbitMqSetup, EventPublicationRepository eventPublicationRepository) {
		this.inflector = inflector;
		this.messageConsumers = messageConsumers;
		this.rabbitMqConfiguration = rabbitMqConfiguration;
		this.rabbitMqSetup = rabbitMqSetup;
		this.eventPublicationRepository = eventPublicationRepository;
	}

	public void consume() {
		try {
			Channel channel = rabbitMqSetup.newConnection().createChannel();
			// Fair dispatch / backpressure
			int prefetch = 20;
			channel.basicQos(prefetch);
			boolean autoAck = false; // manual ack
			log.debug(" [*] Waiting for messages...");
			String consumerTag = "rmq-consumer-" + UUID.randomUUID();
			channel.basicConsume(rabbitMqConfiguration.getQueue(), autoAck, consumerTag, new DeliveryConsumer(channel));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class DeliveryConsumer extends DefaultConsumer {

		private Channel channel;

		/**
		 * Constructs a new instance and records its association to the passed-in channel.
		 *
		 * @param channel the channel to which this consumer is attached
		 */
		public DeliveryConsumer(Channel channel) {
			super(channel);
			this.channel = channel;
		}

		@Override
		public void handleDelivery(String consumerTag,
								   Envelope envelope,
								   AMQP.BasicProperties properties,
								   byte[] body) throws IOException {
			String msg = new String(body, StandardCharsets.UTF_8);
			try {
				// --- Process message ---
				log.debug(" [x] Received: " + msg);
				if (properties.getType() != null) {
					inflector.inflect(properties.getType()).ifPresentOrElse(clz -> {
						try {
							JsonMessage<?> jsonMessage = JacksonEventSerializer.JsonSerializer().readValue(msg, jsonMessageTypeReference(clz));
							for (MessageConsumer messageConsumer : messageConsumers) {
								var message = new Message(jsonMessage.event(), new MetaData(jsonMessage.metaData()));
								messageConsumer.handle(message);
								eventPublicationRepository.markAsCompleted(getHeaderId(message));
							}
						} catch (JsonProcessingException e) {
							throw new RuntimeException(e);
						}
					}, () -> {
						log.debug("No inflected class found {}", properties.getType());
					});
				}

				// Ack on success
				channel.basicAck(envelope.getDeliveryTag(), false);
			} catch (Exception e) {
				log.error("RMQ Consumer Exception", e);
				// Send to DLX by rejecting (no requeue)
				channel.basicNack(envelope.getDeliveryTag(), false, false);
			}
		}

		private UUID getHeaderId(Message message) {
			return message.metaData().containsKey(IdGeneratorMessageDecorator.ID) ? UUID.fromString(message.metaData()
				.get(IdGeneratorMessageDecorator.ID).toString()) : UUID.randomUUID();
		}

		private TypeReference<JsonMessage<?>> jsonMessageTypeReference(Class<?> inflectedClass) {
			return new TypeReference<>() {
				@Override
				public Type getType() {
					return new ParameterizedType() {
						@Override
						public Type[] getActualTypeArguments() {
							return new Type[] {inflectedClass};
						}

						@Override
						public Type getRawType() {
							return JsonMessage.class;
						}

						@Override
						public Type getOwnerType() {
							return JsonMessage.class;
						}
					};
				}
			};
		}
	}

}
