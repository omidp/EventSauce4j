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

package io.eventsauce4j.example;

import io.eventsauce4j.api.event.EventDispatcher;
import io.eventsauce4j.config.EnableEventSauce4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;

@SpringBootApplication
@EnableTransactionManagement
@EnableEventSauce4j
public class EventSauce4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSauce4jApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(EventDispatcher eventDispatcher) {
		return args -> {
			eventDispatcher.dispatch(new OrderStarted(UUID.randomUUID(), "order started"));
		};
	}

}
