/*
 * Copyright 2024–2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.omidp.eventsauce4j.rmq;

import io.github.omidp.eventsauce4j.rabbitmq.RabbitMqConfiguration;
import io.github.omidp.eventsauce4j.testcontainers.rmq.AbstractRabbitMQContainerBaseTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		var config = getRabbitMqConfiguration();
		setProperty("eventsauce4j.rabbitmq.host", config.getHost(), applicationContext);
		setProperty("eventsauce4j.rabbitmq.port", ""+config.getPort(), applicationContext);
		setProperty("eventsauce4j.rabbitmq.username", config.getUsername(), applicationContext);
		setProperty("eventsauce4j.rabbitmq.password", config.getPassword(), applicationContext);
		setProperty("eventsauce4j.rabbitmq.exchange", config.getExchange(), applicationContext);
		setProperty("eventsauce4j.rabbitmq.queue", config.getQueue(), applicationContext);
		setProperty("eventsauce4j.rabbitmq.routingKeys", config.getRoutingKeys(), applicationContext);
		setProperty("eventsauce4j.outbox.delayInterval", ""+2, applicationContext);
		setProperty("eventsauce4j.outbox.lockName", "test-outbox-lock", applicationContext);
		setProperty("eventsauce4j.outbox.enabled", "true", applicationContext);
	}

	private RabbitMqConfiguration getRabbitMqConfiguration() {
		var config =  new RabbitMqConfiguration();
		config.setHost(AbstractRabbitMQContainerBaseTest.RABBITMQ_CONTAINER.getHost());
		config.setUsername(AbstractRabbitMQContainerBaseTest.RABBITMQ_CONTAINER.getAdminUsername());
		config.setPassword(AbstractRabbitMQContainerBaseTest.RABBITMQ_CONTAINER.getAdminPassword());
		config.setPort(AbstractRabbitMQContainerBaseTest.RABBITMQ_CONTAINER.getAmqpPort());
		config.setExchange("eventsauce4j.exchange.test");
		config.setQueue("test.queue");
		config.setRoutingKeys(List.of("order.start"));
		return config;
	}

	private void setProperty(String key, String value, ConfigurableApplicationContext applicationContext) {
		TestPropertyValues.of(key+"="+value).applyTo(applicationContext);
	}

	private void setProperty(String key, List<String> value, ConfigurableApplicationContext applicationContext) {
		String[] pairs = new String[value.size()];
		for (int i = 0; i < value.size(); i++) {
			pairs[i] = "eventsauce4j.rabbitmq.routingKeys[" + i + "]=" + value.get(i);
		}
		TestPropertyValues.of(pairs).applyTo(applicationContext.getEnvironment(), TestPropertyValues.Type.MAP, "eventsauce4j.rabbitmq.routingKeys");
	}
}
