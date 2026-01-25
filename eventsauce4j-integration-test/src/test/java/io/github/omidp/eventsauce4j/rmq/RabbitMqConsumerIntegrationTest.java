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

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.it.OrderStarter;
import io.github.omidp.eventsauce4j.jpa.JpaTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Omid Pourhadi
 */
@SpringJUnitConfig(initializers = {ConfigDataApplicationContextInitializer.class, RabbitMqContextInitializer.class}, classes = {
	RmqTestConfig.class, JpaTestConfig.class
})
@AutoConfigureTestEntityManager
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RabbitMqConsumerIntegrationTest {

	@Autowired
	private EventDispatcher eventDispatcher;

	@Test
	void test(){
		eventDispatcher.dispatch(new OrderStarter(UUID.randomUUID()));
		Awaitility.await().atMost(50, TimeUnit.SECONDS).until(()->false);
	}

}
