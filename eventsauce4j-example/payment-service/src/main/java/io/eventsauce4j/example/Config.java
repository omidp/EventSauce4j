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

package io.github.omidp.example;

import io.github.omidp.api.event.Inflector;
import io.github.omidp.core.inflector.AnnotationInflector;
import io.github.omidp.core.inflector.ChainInflector;
import io.github.omidp.core.inflector.StaticInflector;
import io.github.omidp.example.domain.event.BonusAcquired;
import io.github.omidp.example.domain.event.PaymentUserCreated;
import io.github.omidp.rabbitmq.EnableRabbitMqEventSauce4j;
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
			new StaticInflector(Map.of("payment.public.userCreated", PaymentUserCreated.class)),
			new AnnotationInflector(BonusAcquired.class.getPackageName())
		));
	}

}
