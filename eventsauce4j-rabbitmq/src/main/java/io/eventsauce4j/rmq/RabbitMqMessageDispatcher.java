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

package io.eventsauce4j.rmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.api.message.MessageSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqMessageDispatcher implements MessageDispatcher {


	private final MessageSerializer messageSerializer;

	public RabbitMqMessageDispatcher(MessageSerializer messageSerializer) {
		this.messageSerializer = messageSerializer;
	}

	@Override
	public void dispatch(Message message) {
		try (Connection conn = RabbitConfig.newConnection();
			 Channel ch = conn.createChannel()) {

			RabbitConfig.declareTopology(ch);         // safe to call repeatedly

			// Enable confirms (simple & robust)
			ch.confirmSelect();

			ch.basicPublish(
				RabbitConfig.EXCHANGE,
				RabbitConfig.ROUTING_KEY,
				MessageProperties.PERSISTENT_TEXT_PLAIN,      // make message persistent
				messageSerializer.serialize(message).getBytes(StandardCharsets.UTF_8)
			);

			// wait for all outstanding acks (throws on nack/timeout)
			ch.waitForConfirmsOrDie();

			System.out.println("Published messages with confirms.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}