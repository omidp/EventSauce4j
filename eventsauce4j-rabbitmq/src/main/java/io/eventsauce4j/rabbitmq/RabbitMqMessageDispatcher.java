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

package io.eventsauce4j.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.api.message.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqMessageDispatcher implements MessageDispatcher {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqMessageDispatcher.class);

	private final MessageSerializer messageSerializer;
	private final RabbitMqConfiguration rabbitMqConfiguration;
	private final RabbitMqSetup rabbitMqSetup;

	public RabbitMqMessageDispatcher(MessageSerializer messageSerializer,
									 RabbitMqConfiguration rabbitMqConfiguration,
									 RabbitMqSetup rabbitMqSetup) {
		this.messageSerializer = messageSerializer;
		this.rabbitMqConfiguration = rabbitMqConfiguration;
		this.rabbitMqSetup = rabbitMqSetup;
	}

	@Override
	public void dispatch(Message message) {
		try (Connection conn = rabbitMqSetup.newConnection();
			 Channel ch = conn.createChannel()) {

			// Enable confirms (simple & robust)
			ch.confirmSelect();
			AMQP.BasicProperties messageProperties =
				new AMQP.BasicProperties("application/json",
					null,
					message.getMetaData(),
					2,
					0, null, null, null,
					null, null, message.getEvent().getClass().getName(), null,
					null, null
				);
			ch.basicPublish(
				rabbitMqConfiguration.getExchange(),
				rabbitMqConfiguration.getRoutingKey(),
				messageProperties,
				messageSerializer.serialize(message).getBytes(StandardCharsets.UTF_8)
			);

			// wait for all outstanding acks (throws on nack/timeout)
			ch.waitForConfirmsOrDie();

			log.debug("Published messages with confirms.");
		} catch (Exception e) {
			throw new RabbitMqException(e);
		}
	}
}