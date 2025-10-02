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

package io.eventsauce4j.it;

/**
 * @author Omid Pourhadi
 */

import io.eventsauce4j.api.event.EventDispatcher;
import io.eventsauce4j.api.message.MessageDispatcher;
import io.eventsauce4j.config.EnableEventSauce4j;
import io.eventsauce4j.config.EventSauce4jConfig;
import io.eventsauce4j.core.EventMessage;
import io.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import io.eventsauce4j.core.dispatcher.SynchronousEventMessageDispatcher;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig(initializers = {ConfigDataApplicationContextInitializer.class}, classes = {
	EventDispatcherIntegrationTest.MyTestConfig.class, JpaTestConfig.class
})
@RecordApplicationEvents
@AutoConfigureTestEntityManager
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class EventDispatcherIntegrationTest {

	@Autowired DataSource dataSource;

	@Autowired ApplicationEventPublisher publisher;

	@Test
	void submitOrder(@Autowired OrderService service, ApplicationEvents events) {
		CompletableFuture.runAsync(() -> {
//			publisher.publishEvent(new EventMessage(new OrderStarter(UUID.randomUUID())));
		}).join();
//		publisher.publishEvent(new EventMessage(new OrderStarter(UUID.randomUUID())));
		var jdbcTemp = new JdbcTemplate(dataSource);
		service.submitOrder(new Order(UUID.randomUUID(), "First order."));

//		Awaitility.await()
//			.atMost(Duration.ofSeconds(30))     // ma
//			.until(new Callable<Boolean>() {
//				@Override
//				public Boolean call() throws Exception {
//					return jdbcTemp.queryForObject(
//						"select count(*) from event_publication where completion_date is not null and event_type = ?",
//						Number.class,
//						OrderStarter.class.getName()
//					).intValue() > 0;
//				}
//			});
//
//		int rec = jdbcTemp
//			.queryForObject(
//				"select count(*) from event_publication where completion_date is not null and event_type = ?",
//				Number.class,
//				OrderStarter.class.getName()
//			)
//			.intValue();

//		System.out.println(rec);
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
		long numEvents = events.stream(EventMessage.class).count();
		assertThat(numEvents).isEqualTo(1);
	}

	@EnableEventSauce4j
	@EnableTransactionManagement
	@ComponentScan(basePackageClasses = OrderService.class)
	public static class MyTestConfig {

		@Primary
		@Bean EventDispatcher eventDispatcher(@Qualifier(EventSauce4jConfig.SYNCHRONOUS_EVENT_MESSAGE_DISPATCHER_NAME)MessageDispatcher messageDispatcher){
			return new SynchronousEventDispatcher(messageDispatcher, new IdGeneratorMessageDecorator());
		}
	}

}
