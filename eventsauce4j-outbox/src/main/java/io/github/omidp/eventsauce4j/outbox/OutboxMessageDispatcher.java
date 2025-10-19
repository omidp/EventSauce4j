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

package io.github.omidp.eventsauce4j.outbox;

import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.event.DefaultEventPublication;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Omid Pourhadi
 */
public class OutboxMessageDispatcher implements MessageDispatcher {

	private final Supplier<EventPublicationRepository> eventPublicationRepository;

	public OutboxMessageDispatcher(Supplier<EventPublicationRepository> eventPublicationRepository) {
		this.eventPublicationRepository = eventPublicationRepository;
	}

	@Override
	public void dispatch(Message message) {
		eventPublicationRepository.get().persist(new DefaultEventPublication(message, getHeaderId(message), Instant.now()));
	}

	UUID getHeaderId(Message message) {
		return message.metaData().containsKey(IdGeneratorMessageDecorator.ID) ? UUID.fromString(message.metaData()
			.get(IdGeneratorMessageDecorator.ID).toString()) : UUID.randomUUID();
	}

}
