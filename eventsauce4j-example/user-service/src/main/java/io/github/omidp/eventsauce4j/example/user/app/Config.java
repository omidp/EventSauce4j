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

package io.github.omidp.eventsauce4j.example.user.app;


import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.core.inflector.AnnotationInflector;
import io.github.omidp.eventsauce4j.core.inflector.ChainInflector;
import io.github.omidp.eventsauce4j.core.inflector.ExternalInflector;
import io.github.omidp.eventsauce4j.core.inflector.StaticInflector;
import io.github.omidp.eventsauce4j.example.user.domain.event.EmailSent;
import io.github.omidp.eventsauce4j.example.user.domain.event.UserCreated;
import io.github.omidp.eventsauce4j.rabbitmq.starter.EnableRabbitMqEventSauce4j;
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
	Inflector inflection() {
		return new ChainInflector(List.of(
			new ExternalInflector("io.github.omidp.example.domain.event.external"),
			new AnnotationInflector(UserCreated.class.getPackageName()),
			new StaticInflector(Map.of(
				EmailSent.class.getName(), EmailSent.class
			))
		));
	}
}
