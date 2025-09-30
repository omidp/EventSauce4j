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

import io.eventsauce4j.api.event.EventDispatcher;
import io.eventsauce4j.api.message.MessageDecorator;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.api.outbox.EventPublicationRepository;
import io.eventsauce4j.core.consumer.EventMessageConsumer;
import io.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import io.eventsauce4j.core.dispatcher.OutboxMessageDispatcher;
import io.eventsauce4j.core.dispatcher.SynchronousEventMessageDispatcher;
import io.eventsauce4j.core.dispatcher.SynchronousMessageDispatcher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Omid Pourhadi
 */
@Configuration
@EnableConfigurationProperties({EventSauce4jCustomConfiguration.class})
public class EventSauce4jConfig {

	public static final String SYNCHRONOUS_MESSAGE_DISPATCHER_NAME = "synchronousMessageDispatcher";
	public static final String SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME = "synchronousEventMessageDispatcher";
	public static final String EVENT_MESSAGE_CONSUMER = "eventMessageConsumer";
	public static final String OUTBOX_RELAY = "outboxRelay";
	public static final String OUTBOX_LOCK = "outboxLock";

	@Bean(name = SYNCHRONOUS_MESSAGE_DISPATCHER_NAME)
	MessageDispatcher synchronousMessageDispatcher() {
		return new SynchronousMessageDispatcher();
	}

	@Bean(name = SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME)
	MessageDispatcher synchronousEventMessageDispatcher() {
		return new SynchronousEventMessageDispatcher();
	}

	@Bean
	EventDispatcher eventDispatcher(MessageDecorator messageDecorator,
									ApplicationContext applicationContext) {
		return new SynchronousEventDispatcher(
			new OutboxMessageDispatcher(() -> applicationContext.getBean(EventPublicationRepository.class)),
			messageDecorator
		);
	}

	@Bean
	MessageDecorator messageDecorator() {
		return new IdGeneratorMessageDecorator();
	}


	@Bean(name = EVENT_MESSAGE_CONSUMER)
	EventMessageConsumer eventMessageConsumer() {
		return new EventMessageConsumer();
	}


	@Bean
	EventSauce4jInitializer eventSauce4jInitializer() {
		return new EventSauce4jInitializer();
	}

}
