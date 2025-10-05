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

import io.eventsauce4j.api.event.Event;
import io.eventsauce4j.api.event.Inflector;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Omid Pourhadi
 */
public class AnnotationInflector implements Inflector {

	private Map<String, Class<?>> mapping = new HashMap<>();

	public AnnotationInflector(String... packages) {
		this(List.of(packages));
	}

	public AnnotationInflector(Collection<String> packages) {
		ClassPathScanningCandidateComponentProvider provider =
			new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Event.class));
		for (String pkg : packages) {
			for (BeanDefinition candidate : provider.findCandidateComponents(pkg)) {
				String className = candidate.getBeanClassName();
				try {
					Class<?> clazz = Class.forName(className);
					if (clazz.isAnnotationPresent(Event.class)) {
						Event an = clazz.getAnnotation(Event.class);
						mapping.put(an.routingKey().isEmpty() ? clazz.getName() : an.routingKey(), clazz);
					}
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException("Failed to load class: " + className, e);
				}
			}
		}

	}

	@Override
	public Optional<Class<?>> inflect(String routingKey) {
		return Optional.ofNullable(mapping.get(routingKey));
	}
}
