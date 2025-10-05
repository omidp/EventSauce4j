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

import io.eventsauce4j.core.ExternalInflection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Omid Pourhadi
 */
public class ExternalInflectionTest {

	private ExternalInflection externalInflection;

	@BeforeEach
	void setUp() {
		externalInflection = new ExternalInflection("io.eventsauce4j.domain");
	}

	@Test
	void test() {
		Optional<Class<?>> inflectedClass = externalInflection.getInflectedClass("payment.UserPaid");
		assertTrue(inflectedClass.isPresent());
	}
}
