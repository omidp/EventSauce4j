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

package io.eventsauce4j.config;

import io.eventsauce4j.api.event.Inflection;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.api.outbox.EventPublicationRepository;
import io.eventsauce4j.api.outbox.OutboxRelay;
import io.eventsauce4j.api.outbox.dlq.DeadLetter;
import io.eventsauce4j.api.outbox.lock.OutboxLock;
import io.eventsauce4j.core.EventSauce4jCustomConfiguration;
import io.eventsauce4j.core.dispatcher.MessageDispatcherChain;
import io.eventsauce4j.core.outbox.backoff.SimpleBackOffStrategy;
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
@ConditionalOnProperty(havingValue = "JPA", prefix = "eventsauce4j", name = "persistence", matchIfMissing = true)
@AutoConfigurationPackage(basePackageClasses = JpaEventPublication.class)
public class EventSauce4jJpaConfiguration {

	@Bean
	EventPublicationRepository jpaEventPublicationRepository(EntityManager entityManager, ApplicationContext ctx) {
		return new JpaEventPublicationRepository(new JacksonEventSerializer(), entityManager, () -> ctx.getBean(Inflection.class));
	}

	@Bean(name = OUTBOX_RELAY)
	OutboxRelay outboxRelay(EntityManager em,
							@Qualifier(SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME) MessageDispatcher synchronousEventMessageDispatcher,
							@Qualifier("jpaEventPublicationRepository") EventPublicationRepository eventPublicationRepository) {
		return new DatabaseOutboxRelay(
			eventPublicationRepository,
			new MessageDispatcherChain(List.of(synchronousEventMessageDispatcher)),
			new SimpleBackOffStrategy(3, Duration.ofSeconds(5)),
			new MarkMessagesConsumedOnCommit(),
			deadLetterQueue(em)
		);
	}


	@Bean
	DeadLetter deadLetterQueue(EntityManager entityManager) {
		return new JpaDeadLetter(entityManager, new JacksonEventSerializer());
	}

	@Bean(name = OUTBOX_LOCK)
	OutboxLock outboxLock(EntityManager entityManager, EventSauce4jCustomConfiguration config) {
		return new DatabaseOutboxLock(entityManager, config.getOutboxLockName());
	}


}
