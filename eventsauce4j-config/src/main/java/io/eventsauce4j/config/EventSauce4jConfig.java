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

import io.eventsauce4j.core.consumer.EventMessageConsumer;
import io.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import io.eventsauce4j.core.dispatcher.MessageDispatcherChain;
import io.eventsauce4j.core.dispatcher.OutboxMessageDispatcher;
import io.eventsauce4j.core.dispatcher.SynchronousEventMessageDispatcher;
import io.eventsauce4j.core.dispatcher.SynchronousMessageDispatcher;
import io.eventsauce4j.api.event.EventDispatcher;
import io.eventsauce4j.jackson.JacksonMessageConverter;
import io.eventsauce4j.jpa.outbox.JpaEventPublicationRepository;
import io.eventsauce4j.jpa.outbox.dlq.JpaDeadLetter;
import io.eventsauce4j.jpa.outbox.lock.DatabaseOutboxLock;
import io.eventsauce4j.jpa.outbox.relay.DatabaseOutboxRelay;
import io.eventsauce4j.api.message.MessageDecorator;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.api.outbox.EventPublicationRepository;
import io.eventsauce4j.api.outbox.OutboxRelay;
import io.eventsauce4j.core.outbox.backoff.WaitBackOffStrategy;
import io.eventsauce4j.api.outbox.dlq.DeadLetter;
import io.eventsauce4j.api.outbox.lock.OutboxLock;
import io.eventsauce4j.core.outbox.relay.MarkMessagesConsumedOnCommit;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.time.Duration;
import java.util.List;

/**
 * @author Omid Pourhadi
 */
@Configuration
public class EventSauce4jConfig {

	public static final String SYNCHRONOUS_MESSAGE_DISPATCHER_NAME = "synchronousMessageDispatcher";
	public static final String SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME = "synchronousEventMessageDispatcher";
	public static final String OUTBOX_MESSAGE_DISPATCHER = "outboxMessageDispatcher";
	public static final String EVENT_MESSAGE_CONSUMER = "eventMessageConsumer";

	@Bean(name = SYNCHRONOUS_MESSAGE_DISPATCHER_NAME)
	MessageDispatcher synchronousMessageDispatcher() {
		return new SynchronousMessageDispatcher();
	}

	@Bean(name = SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME)
	MessageDispatcher synchronousEventMessageDispatcher() {
		return new SynchronousEventMessageDispatcher();
	}

	@Bean(name = OUTBOX_MESSAGE_DISPATCHER)
	MessageDispatcher outboxMessageDispatcher(EventPublicationRepository eventPublicationRepository) {
		return new OutboxMessageDispatcher(eventPublicationRepository);
	}

	@Bean
	EventPublicationRepository eventPublicationRepository(EntityManager entityManager) {
		return new JpaEventPublicationRepository(new JacksonMessageConverter(), entityManager);
	}

	@Bean
	MessageDecorator messageDecorator() {
		return new IdGeneratorMessageDecorator();
	}

	@Bean
	@DependsOn(SYNCHRONOUS_MESSAGE_DISPATCHER_NAME)
	EventDispatcher eventDispatcher(EntityManager em, MessageDecorator messageDecorator) {
		return new SynchronousEventDispatcher(outboxMessageDispatcher(eventPublicationRepository(em)), messageDecorator);
	}

	@Bean(name = EVENT_MESSAGE_CONSUMER)
	EventMessageConsumer eventMessageConsumer() {
		return new EventMessageConsumer();
	}

	@Bean
	OutboxRelay outboxRelay(EntityManager em) {
		return new DatabaseOutboxRelay(
			eventPublicationRepository(em),
			new MessageDispatcherChain(List.of(synchronousMessageDispatcher(), synchronousEventMessageDispatcher())),
			new WaitBackOffStrategy(3, Duration.ofSeconds(5)),
			new MarkMessagesConsumedOnCommit(),
			deadLetterQueue(em)
		);
	}

	@Bean
	DeadLetter deadLetterQueue(EntityManager entityManager) {
		return new JpaDeadLetter(entityManager, new JacksonMessageConverter());
	}

	@Bean
	OutboxLock outboxLock(EntityManager entityManager) {
		return new DatabaseOutboxLock(entityManager);
	}

	@Bean
	EventSauce4jInitializer eventSauce4jInitializer() {
		return new EventSauce4jInitializer();
	}

}
