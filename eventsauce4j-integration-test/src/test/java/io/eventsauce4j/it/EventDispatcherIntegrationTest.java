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

import io.eventsauce4j.config.EnableEventSauce4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig(initializers = ConfigDataApplicationContextInitializer.class)
@RecordApplicationEvents
@EnableEventSauce4j
@TestPropertySource(properties = {
	"eventsauce4j.persistence=JPA",
	"key2 = value2"
})
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
public class EventDispatcherIntegrationTest {


	@Test
	void submitOrder(@Autowired OrderService service, ApplicationEvents events) {
		service.submitOrder(new Order(UUID.randomUUID(), "First order."));
		long numEvents = events.stream(OrderStarter.class).count();
		assertThat(numEvents).isEqualTo(1);
	}

}
