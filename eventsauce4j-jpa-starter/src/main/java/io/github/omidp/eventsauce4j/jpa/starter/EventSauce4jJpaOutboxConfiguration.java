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

package io.github.omidp.eventsauce4j.jpa.starter;

import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.api.outbox.Jitter;
import io.github.omidp.eventsauce4j.api.outbox.OutboxRelay;
import io.github.omidp.eventsauce4j.api.outbox.Sleeper;
import io.github.omidp.eventsauce4j.api.outbox.backoff.BackOffStrategy;
import io.github.omidp.eventsauce4j.api.outbox.dlq.DeadLetter;
import io.github.omidp.eventsauce4j.api.outbox.lock.OutboxLock;
import io.github.omidp.eventsauce4j.api.outbox.relay.RelayCommitStrategy;
import io.github.omidp.eventsauce4j.core.EventSauce4jCustomConfiguration;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import io.github.omidp.eventsauce4j.jpa.outbox.dlq.JpaDeadLetter;
import io.github.omidp.eventsauce4j.jpa.outbox.lock.JpaOutboxLock;
import io.github.omidp.eventsauce4j.outbox.OutboxRelayer;
import io.github.omidp.eventsauce4j.outbox.backoff.ExponentialBackOffStrategy;
import io.github.omidp.eventsauce4j.outbox.backoff.SimpleBackOffStrategy;
import io.github.omidp.eventsauce4j.outbox.relay.DeleteMessageOnCommit;
import io.github.omidp.eventsauce4j.outbox.relay.MarkMessagesConsumedOnCommit;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.github.omidp.eventsauce4j.core.EventSauce4jConfig.SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME;

/**
 * @author Omid Pourhadi
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(havingValue = "true", prefix = "eventsauce4j.outbox", name = "enabled", matchIfMissing = true)
public class EventSauce4jJpaOutboxConfiguration {

	@Bean
	OutboxRelay outboxRelay(EntityManager em,
							@Qualifier(SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME) MessageDispatcher synchronousEventMessageDispatcher,
							EventPublicationRepository eventPublicationRepository,
							RelayCommitStrategy relayCommitStrategy, BackOffStrategy backOffStrategy) {
		return new OutboxRelayer(
			eventPublicationRepository,
			synchronousEventMessageDispatcher,
			backOffStrategy,
			relayCommitStrategy,
			deadLetterQueue(em)
		);
	}

	@Bean
	@ConditionalOnProperty(havingValue = "exponential", prefix = "eventsauce4j", name = "backoff")
	BackOffStrategy exponentialBackOffStrategy(EventSauce4jCustomConfiguration config) {
		return new ExponentialBackOffStrategy(config.getSimpleBackoffMaxRetries(), 2000, 2.0, 5000, Jitter.create(), Sleeper.create());
	}

	@Bean
	@ConditionalOnProperty(havingValue = "simple", prefix = "eventsauce4j", name = "backoff", matchIfMissing = true)
	BackOffStrategy simpleBackOffStrategy(EventSauce4jCustomConfiguration config) {
		return new SimpleBackOffStrategy(config.getSimpleBackoffMaxRetries(), Duration.ofSeconds(config.getSimpleBackoffDelay()));
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

	@Bean
	OutboxLock outboxLock(EntityManager entityManager, EventSauce4jCustomConfiguration config) {
		return new JpaOutboxLock(entityManager, config.getOutboxLockName());
	}


}
