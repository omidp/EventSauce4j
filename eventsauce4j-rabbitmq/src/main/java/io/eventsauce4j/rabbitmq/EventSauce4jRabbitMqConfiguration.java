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

import io.eventsauce4j.api.event.Inflection;
import io.eventsauce4j.api.message.MessageConsumer;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.api.outbox.EventPublicationRepository;
import io.eventsauce4j.api.outbox.OutboxRelay;
import io.eventsauce4j.api.outbox.dlq.DeadLetter;
import io.eventsauce4j.api.outbox.lock.OutboxLock;
import io.eventsauce4j.api.outbox.relay.RelayCommitStrategy;
import io.eventsauce4j.core.EventSauce4jCustomConfiguration;
import io.eventsauce4j.core.dispatcher.MessageDispatcherChain;
import io.eventsauce4j.core.outbox.backoff.SimpleBackOffStrategy;
import io.eventsauce4j.core.outbox.relay.DeleteMessageOnCommit;
import io.eventsauce4j.core.outbox.relay.MarkMessagesConsumedOnCommit;
import io.eventsauce4j.jackson.JacksonEventSerializer;
import io.eventsauce4j.jpa.outbox.JpaEventPublication;
import io.eventsauce4j.jpa.outbox.JpaEventPublicationRepository;
import io.eventsauce4j.jpa.outbox.dlq.JpaDeadLetter;
import io.eventsauce4j.jpa.outbox.lock.DatabaseOutboxLock;
import io.eventsauce4j.jpa.outbox.relay.DatabaseOutboxRelay;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

import static io.eventsauce4j.core.EventSauce4jConfig.OUTBOX_LOCK;
import static io.eventsauce4j.core.EventSauce4jConfig.OUTBOX_RELAY;
import static io.eventsauce4j.core.EventSauce4jConfig.SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME;

/**
 * @author Omid Pourhadi
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(havingValue = "RMQ", prefix = "eventsauce4j", name = "persistence", matchIfMissing = true)
@AutoConfigurationPackage(basePackageClasses = JpaEventPublication.class)
@EnableConfigurationProperties({RabbitMqConfiguration.class})
public class EventSauce4jRabbitMqConfiguration {

	private static final String RMQ_MESSAGE_DISPATCHER = "rabbitMqMessageDispatcher";
	private static final String PUBLICATION_REPO = "jpaEventPublicationRepository";

	@Bean
	RabbitMqSetup rabbitMqSetup(RabbitMqConfiguration config) {
		RabbitMqSetup rabbitMqSetup = new RabbitMqSetup(config);
		rabbitMqSetup.declareTopology();
		return rabbitMqSetup;
	}

	@Bean(RMQ_MESSAGE_DISPATCHER)
	MessageDispatcher rabbitMqMessageDispatcher(RabbitMqConfiguration rabbitMqConfiguration, RabbitMqSetup rabbitMqSetup, Inflection inflection) {
		return new RabbitMqMessageDispatcher(new JacksonEventSerializer(), rabbitMqConfiguration, rabbitMqSetup, inflection);
	}

	@Bean
	RabbitMqConsumer rabbitMqConsumer(List<MessageConsumer> messageConsumers, Inflection inflection, RabbitMqSetup rabbitMqSetup, RabbitMqConfiguration rabbitMqConfiguration) {
		return new RabbitMqConsumer(inflection, messageConsumers, rabbitMqConfiguration, rabbitMqSetup);
	}

	@Bean(PUBLICATION_REPO)
	EventPublicationRepository jpaEventPublicationRepository(EntityManager entityManager, ApplicationContext ctx) {
		return new JpaEventPublicationRepository(new JacksonEventSerializer(), entityManager, () -> ctx.getBean(Inflection.class));
	}

	@Bean(name = OUTBOX_RELAY)
	OutboxRelay outboxRelay(EntityManager em,
							@Qualifier(SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME) MessageDispatcher synchronousEventMessageDispatcher,
							@Qualifier(RMQ_MESSAGE_DISPATCHER) MessageDispatcher rabbitMqMessageDispatcher,
							@Qualifier(PUBLICATION_REPO) EventPublicationRepository eventPublicationRepository,
							RelayCommitStrategy relayCommitStrategy) {
		return new DatabaseOutboxRelay(
			eventPublicationRepository,
			new MessageDispatcherChain(List.of(synchronousEventMessageDispatcher, rabbitMqMessageDispatcher)),
			new SimpleBackOffStrategy(3, Duration.ofSeconds(5)),
			relayCommitStrategy,
			deadLetterQueue(em)
		);
	}

	@Bean
	@ConditionalOnProperty(havingValue = "true", prefix = "eventsauce4j", name = "archive", matchIfMissing = true)
	RelayCommitStrategy markMessagesConsumedOnCommit() {
		return new MarkMessagesConsumedOnCommit();
	}

	@Bean
	@ConditionalOnProperty(havingValue = "false", prefix = "eventsauce4j", name = "archive")
	RelayCommitStrategy deleteMessageOnCommit() {
		return new DeleteMessageOnCommit();
	}


	@Bean
	DeadLetter deadLetterQueue(EntityManager entityManager) {
		return new JpaDeadLetter(entityManager, new JacksonEventSerializer());
	}

	@Bean(name = OUTBOX_LOCK)
	OutboxLock outboxLock(EntityManager entityManager) {
		return new DatabaseOutboxLock(entityManager);
	}

}
