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

package io.github.omidp.eventsauce4j.core;

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.message.MessageDecorator;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.consumer.EventMessageConsumer;
import io.github.omidp.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import io.github.omidp.eventsauce4j.core.dispatcher.OutboxMessageDispatcher;
import io.github.omidp.eventsauce4j.core.dispatcher.SynchronousEventMessageDispatcher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Omid Pourhadi
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({EventSauce4jCustomConfiguration.class})
public class EventSauce4jConfig {

	public static final String SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME = "synchronousEventMessageDispatcher";
	public static final String OUTBOX_RELAY = "outboxRelay";
	public static final String OUTBOX_LOCK = "outboxLock";

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


	@Bean
	EventMessageConsumer eventMessageConsumer(List<MessageConsumer> consumers, ApplicationContext applicationContext) {
		return new EventMessageConsumer(consumers, () -> applicationContext.getBean(EventPublicationRepository.class));
	}

	@Bean
	public static EventSauce4jInitializer eventSauce4jInitializer() {
		return new EventSauce4jInitializer();
	}

}
