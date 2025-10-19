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

package io.github.omidp.eventsauce4j.rabbitmq.starter;

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.message.MessageDecorator;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import io.github.omidp.eventsauce4j.jpa.outbox.JpaEventPublicationRepository;
import io.github.omidp.eventsauce4j.outbox.OutboxMessageDispatcher;
import io.github.omidp.eventsauce4j.rabbitmq.RabbitMqConfiguration;
import io.github.omidp.eventsauce4j.rabbitmq.RabbitMqConsumerFactory;
import io.github.omidp.eventsauce4j.rabbitmq.RabbitMqSetup;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Omid Pourhadi
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(havingValue = "RMQ", prefix = "eventsauce4j", name = "persistence", matchIfMissing = true)
@AutoConfigurationPackage(basePackages = {"io.github.omidp.eventsauce4j.jpa.outbox"})
@EnableConfigurationProperties({RabbitMqConfiguration.class})
public class EventSauce4jRabbitMqConfiguration {

	private static final String PUBLICATION_REPO = "jpaEventPublicationRepository";

	@Bean
	EventDispatcher eventDispatcher(EventPublicationRepository eventPublicationRepository, MessageDecorator messageDecorator) {
		return new SynchronousEventDispatcher(new OutboxMessageDispatcher(() -> eventPublicationRepository), messageDecorator);
	}

	@Bean(PUBLICATION_REPO)
	EventPublicationRepository jpaEventPublicationRepository(EntityManager entityManager) {
		return new JpaEventPublicationRepository(new JacksonEventSerializer(), entityManager);
	}

	@Bean
	RabbitMqSetup rabbitMqSetup(RabbitMqConfiguration config) {
		RabbitMqSetup rabbitMqSetup = new RabbitMqSetup(config);
		rabbitMqSetup.declareTopology();
		return rabbitMqSetup;
	}

	@Bean
	RabbitMqConsumerFactory rabbitMqConsumer(RabbitMqSetup rabbitMqSetup, RabbitMqConfiguration rabbitMqConfig,
											 List<MessageConsumer> messageConsumers, Inflector inflector,
											 EventPublicationRepository eventPublicationRepository) {
		var consumer = new RabbitMqConsumerFactory(rabbitMqSetup, rabbitMqConfig, messageConsumers, inflector,
			eventPublicationRepository, new JacksonEventSerializer()
		);
		consumer.build();
		return consumer;
	}

}