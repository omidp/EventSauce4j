package io.github.omidp.eventsauce4j.rabbitmq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.github.omidp.eventsauce4j.api.event.EventSerializer;
import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.event.MetaDataFieldExtractorFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqConsumerFactory {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqConsumerFactory.class);

	private final RabbitMqSetup rabbitMqSetup;
	private final RabbitMqConfiguration rabbitMqConfiguration;
	private final List<MessageConsumer> messageConsumers;
	private final Inflector inflector;
	private final EventPublicationRepository eventPublicationRepository;
	private final EventSerializer eventSerializer;

	public RabbitMqConsumerFactory(RabbitMqSetup rabbitMqSetup, RabbitMqConfiguration rabbitMqConfiguration,
								   List<MessageConsumer> messageConsumers, Inflector inflector,
								   EventPublicationRepository eventPublicationRepository, EventSerializer eventSerializer) {
		this.rabbitMqSetup = rabbitMqSetup;
		this.rabbitMqConfiguration = rabbitMqConfiguration;
		this.messageConsumers = messageConsumers;
		this.inflector = inflector;
		this.eventPublicationRepository = eventPublicationRepository;
		this.eventSerializer = eventSerializer;
	}

	public void build() {
		try {
			Channel channel = rabbitMqSetup.newConnection().createChannel();
			// Fair dispatch / backpressure
			int prefetch = 20;
			channel.basicQos(prefetch);
			boolean autoAck = false; // manual ack
			log.debug(" [*] Waiting for messages...");
			String consumerTag = "rmq-consumer-" + UUID.randomUUID();
			channel.basicConsume(
				rabbitMqConfiguration.getQueue(),
				autoAck,
				consumerTag,
				new DefaultConsumerImpl(channel)
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private class DefaultConsumerImpl extends DefaultConsumer {
		private Channel channel;

		/**
		 * Constructs a new instance and records its association to the passed-in channel.
		 *
		 * @param channel the channel to which this consumer is attached
		 */
		public DefaultConsumerImpl(Channel channel) {
			super(channel);
			this.channel = channel;
		}

		@Override
		public void handleDelivery(String consumerTag,
								   Envelope envelope,
								   AMQP.BasicProperties properties,
								   byte[] body) throws IOException {
			try {
				if (properties.getType() != null) {
					inflector.inflect(properties.getType()).ifPresentOrElse(clz -> {
						Map<String, Object> headers = properties.getHeaders();
						String content = new String(body, StandardCharsets.UTF_8);
						log.debug(" [x] Received: " + content);
						Object event = eventSerializer.deserialize(content, clz);
						for (MessageConsumer messageConsumer : messageConsumers) {
							var message = new Message(event, new MetaData(headers));
							messageConsumer.handle(message);
							eventPublicationRepository.markAsCompleted(
								UUID.fromString(MetaDataFieldExtractorFunction.getId().apply(message.metaData()).get()));
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