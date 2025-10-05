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

package io.eventsauce4j.unit.test;

import io.eventsauce4j.api.event.Inflector;
import io.eventsauce4j.core.inflector.AnnotationInflector;
import io.eventsauce4j.core.inflector.StaticInflector;
import io.eventsauce4j.domain.EmailSent;
import io.eventsauce4j.domain.UserCreated;
import io.eventsauce4j.domain.external.UserPaid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Omid Pourhadi
 */
public class AnnotationInflectorTest {

	private Inflector externalInflection;

	@BeforeEach
	void setUp() {
		externalInflection = new AnnotationInflector(EmailSent.class.getPackageName());
	}

	@Test
	void testInflectEvent() {
		Optional<Class<?>> inflectedClass = externalInflection.inflect("user.email");
		assertTrue(inflectedClass.isPresent());
		Optional<Class<?>> inflectedClass2 = externalInflection.inflect(UserCreated.class.getName());
		assertTrue(inflectedClass2.isPresent());
	}

	@Test
	void testInflectEventNotPresent() {
		Optional<Class<?>> inflectedClass = externalInflection.inflect(UserPaid.class.getName());
		assertFalse(inflectedClass.isPresent());
	}
}
