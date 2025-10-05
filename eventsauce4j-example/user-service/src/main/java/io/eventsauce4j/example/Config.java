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

import io.eventsauce4j.api.event.Inflection;
import io.eventsauce4j.core.ChainInflection;
import io.eventsauce4j.core.DefaultInflection;
import io.eventsauce4j.core.ExternalInflection;
import io.eventsauce4j.example.domain.event.EmailSent;
import io.eventsauce4j.example.domain.event.UserCreated;
import io.eventsauce4j.rabbitmq.EnableRabbitMqEventSauce4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * @author Omid Pourhadi
 */
@Configuration
//@EnableJpaEventSauce4j
@EnableRabbitMqEventSauce4j
public class Config {

	@Bean
	Inflection inflection() {
		return new ChainInflection(List.of(
			new ExternalInflection("io.eventsauce4j.example.domain.event.external"),
			new DefaultInflection(Map.of(
				UserCreated.class.getName(), UserCreated.class,
				EmailSent.class.getName(), EmailSent.class
			))
		));
	}
}
