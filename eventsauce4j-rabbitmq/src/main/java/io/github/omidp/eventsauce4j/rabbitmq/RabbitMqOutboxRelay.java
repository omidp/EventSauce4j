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

import com.rabbitmq.client.Channel;
import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.api.outbox.OutboxRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqOutboxRelay implements OutboxRelay {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqOutboxRelay.class);


	private final RabbitMqSetup rabbitMqSetup;
	private final RabbitMqConfiguration rabbitMqConfiguration;
	private final OutboxRelay databaseOutboxRelay;
	private final List<MessageConsumer> messageConsumers;
	private final Inflector inflector;
	private final EventPublicationRepository eventPublicationRepository;

	public RabbitMqOutboxRelay(RabbitMqSetup rabbitMqSetup, RabbitMqConfiguration rabbitMqConfiguration, OutboxRelay databaseOutboxRelay, List<MessageConsumer> messageConsumers, Inflector inflector, EventPublicationRepository eventPublicationRepository) {
		this.rabbitMqSetup = rabbitMqSetup;
		this.rabbitMqConfiguration = rabbitMqConfiguration;
		this.databaseOutboxRelay = databaseOutboxRelay;
		this.messageConsumers = messageConsumers;
		this.inflector = inflector;
		this.eventPublicationRepository = eventPublicationRepository;
	}


	public void initConsumer() {
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
				new RabbitMqConsumer(channel, messageConsumers, inflector, eventPublicationRepository)
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void publish() {
		initConsumer();
		databaseOutboxRelay.publish();
	}


}
