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

package io.eventsauce4j.core;

import io.eventsauce4j.api.event.EventPublication;
import io.eventsauce4j.api.event.Status;
import io.eventsauce4j.api.message.Message;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class DefaultEventPublication implements EventPublication {

	private final Message message;
	private final UUID id;

	public DefaultEventPublication(Message message, UUID id) {
		this.message = message;
		this.id = id;
	}

	@Override
	public UUID getIdentifier() {
		return id;
	}

	@Override
	public Message getMessage() {
		return message;
	}

	@Override
	public Instant getPublicationDate() {
		return Instant.now();
	}

	@Override
	public Optional<Instant> getCompletionDate() {
		return Optional.empty();
	}

	@Override
	public Status getStatus() {
		return Status.PROCESSING;
	}

	@Override
	public Instant getLastResubmissionDate() {
		return null;
	}

	@Override
	public int getCompletionAttempts() {
		return 0;
	}
}
