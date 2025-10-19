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

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMqSetup {

	private final RabbitMqConfiguration rabbitMqConfiguration;

	public RabbitMqSetup(RabbitMqConfiguration rabbitMqConfiguration) {
		this.rabbitMqConfiguration = rabbitMqConfiguration;
	}

	public Connection newConnection() throws Exception {
		ConnectionFactory f = new ConnectionFactory();
		f.setHost(rabbitMqConfiguration.getHost());
		f.setPort(rabbitMqConfiguration.getPort());
		f.setVirtualHost(rabbitMqConfiguration.getVhost());
		f.setUsername(rabbitMqConfiguration.getUsername());
		f.setPassword(rabbitMqConfiguration.getPassword());
		f.setAutomaticRecoveryEnabled(true);     // auto-recover connections
		f.setNetworkRecoveryInterval(5000);
		return f.newConnection();
	}

	public void declareTopology() {
		try (Connection con = newConnection(); Channel channel = con.createChannel()) {
			declareTopology(channel);
		} catch (IOException e) {
			throw new RabbitMqException(e);
		} catch (TimeoutException e) {
			throw new RabbitMqException(e);
		} catch (Exception e) {
			throw new RabbitMqException(e);
		}
	}

	/**
	 * Idempotently declares exchange/queue/bindings. Call once on startup (producer or consumer).
	 */
	private void declareTopology(Channel channel) throws Exception {
		channel.exchangeDeclare(rabbitMqConfiguration.getDlx(), BuiltinExchangeType.FANOUT, true);
		channel.queueDeclare(rabbitMqConfiguration.getDlq(), true, false, false, null);
		channel.queueBind(rabbitMqConfiguration.getDlq(), rabbitMqConfiguration.getDlx(), "");

		// Main exchange
		channel.exchangeDeclare(rabbitMqConfiguration.getExchange(), BuiltinExchangeType.DIRECT, true);

		// Queue arguments: dead-letter + TTL examples (tune as needed)
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", rabbitMqConfiguration.getDlx());  // send rejected/expired here
		args.put("x-message-ttl", rabbitMqConfiguration.getMessageTtl());       // 5 minutes (optional)
		args.put("x-max-length", 50_000);         // cap queue length (optional)

		// Durable, non-exclusive, not auto-delete
		channel.queueDeclare(rabbitMqConfiguration.getQueue(), true, false, false, args);
		for (String routingKey : rabbitMqConfiguration.getRoutingKeys()) {
			channel.queueBind(rabbitMqConfiguration.getQueue(), rabbitMqConfiguration.getExchange(), routingKey);
		}
	}
}
