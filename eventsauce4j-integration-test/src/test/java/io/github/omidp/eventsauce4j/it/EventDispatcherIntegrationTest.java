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

package io.github.omidp.eventsauce4j.it;

/**
 * @author Omid Pourhadi
 */

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import io.github.omidp.eventsauce4j.jpa.starter.EnableJpaEventSauce4j;
import io.github.omidp.eventsauce4j.core.event.EventMessage;
import io.github.omidp.eventsauce4j.core.EventSauce4jConfig;
import io.github.omidp.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig(initializers = {ConfigDataApplicationContextInitializer.class}, classes = {
	EventDispatcherIntegrationTest.MyTestConfig.class, JpaTestConfig.class
})
@RecordApplicationEvents
@AutoConfigureTestEntityManager
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class EventDispatcherIntegrationTest {

	@Test
	void submitOrder(@Autowired OrderService service, ApplicationEvents events) {
		service.submitOrder(new Order(UUID.randomUUID(), "First order."));
		long numEvents = events.stream(EventMessage.class).count();
		assertThat(numEvents).isEqualTo(1);
	}

	@EnableJpaEventSauce4j
	@EnableTransactionManagement
	@ComponentScan(basePackageClasses = OrderService.class)
	public static class MyTestConfig {

		@Primary
		@Bean EventDispatcher eventDispatcher(@Qualifier(EventSauce4jConfig.SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME)MessageDispatcher messageDispatcher){
			return new SynchronousEventDispatcher(messageDispatcher, new IdGeneratorMessageDecorator());
		}
	}

}
