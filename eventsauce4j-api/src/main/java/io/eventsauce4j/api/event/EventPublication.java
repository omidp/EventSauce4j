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

package io.eventsauce4j.api.event;

import io.eventsauce4j.api.message.Message;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public interface EventPublication {

	UUID getIdentifier();

	Message getMessage();

	Instant getPublicationDate();

	Optional<Instant> getCompletionDate();

	default boolean isCompleted() {
		return getCompletionDate().isPresent();
	}

	Status getStatus();

	Instant getLastResubmissionDate();

	int getCompletionAttempts();

	default int compareTo(EventPublication that) {
		return this.getPublicationDate().compareTo(that.getPublicationDate());
	}

	default String getRoutingKey() {
		Class<?> clz = getMessage().event().getClass();
		if (clz.isAnnotationPresent(ExternalEvent.class)) {
			return clz.getAnnotation(ExternalEvent.class).routingKey();
		}
		return clz.getName();
	}

}