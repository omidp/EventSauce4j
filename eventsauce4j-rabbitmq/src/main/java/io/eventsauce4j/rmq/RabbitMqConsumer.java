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

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageConsumer;
import io.eventsauce4j.api.message.MessageDeserializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqConsumer {

	private final MessageDeserializer messageDeserializer;
	private final Connection conn;
	private final Channel ch;
	private List<MessageConsumer> messageConsumers = new ArrayList<>();

	public RabbitMqConsumer(MessageDeserializer messageDeserializer) {
		this.messageDeserializer = messageDeserializer;
		try {
			this.conn = RabbitConfig.newConnection();
			this.ch = conn.createChannel();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setMessageConsumers(List<MessageConsumer> messageConsumers) {
		this.messageConsumers = messageConsumers;
	}

	public void consume() {
		try {

				RabbitConfig.declareTopology(ch);

				// Fair dispatch / backpressure
				int prefetch = 20;
				ch.basicQos(prefetch);

				boolean autoAck = false; // manual ack
				System.out.println(" [*] Waiting for messages...");
				ch.basicConsume(RabbitConfig.QUEUE, autoAck, "demo-consumer",
					new DefaultConsumer(ch) {
						@Override
						public void handleDelivery(String consumerTag,
												   Envelope envelope,
												   AMQP.BasicProperties properties,
												   byte[] body) throws IOException {
							String msg = new String(body, StandardCharsets.UTF_8);
							try {
								// --- Process message ---
								System.out.println(" [x] Received: " + msg);
//								Message deserialize = messageDeserializer.deserialize(msg, Message.class);
//								for (MessageConsumer messageConsumer : messageConsumers) {
//									messageConsumer.handle(deserialize);
//								}

								// simulate work: doWork(msg);

								// Ack on success
								ch.basicAck(envelope.getDeliveryTag(), false);
							} catch (Exception e) {
								e.printStackTrace();
								// Send to DLX by rejecting (no requeue)
								ch.basicNack(envelope.getDeliveryTag(), false, false);
							}
						}
					}
				);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
