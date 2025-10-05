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

package io.eventsauce4j.core.inflector;

import io.eventsauce4j.api.event.Inflector;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author Omid Pourhadi
 */
public class StaticInflector implements Inflector {

	private final Map<String, Class<?>> mapping;

	public StaticInflector(Map<String, Class<?>> mapping) {
		this.mapping = Collections.unmodifiableMap(mapping);
	}

	@Override
	public Optional<Class<?>> inflect(String routingKey) {
		return Optional.ofNullable(mapping.get(routingKey));
	}
}