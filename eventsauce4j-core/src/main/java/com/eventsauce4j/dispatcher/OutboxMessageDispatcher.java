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

package com.eventsauce4j.dispatcher;

import com.eventsauce4j.DefaultEventPublication;
import com.eventsauce4j.decorator.IdGeneratorMessageDecorator;
import com.eventsauce4j.message.Message;
import com.eventsauce4j.message.MessageDispatcher;
import com.eventsauce4j.outbox.EventPublicationRepository;

import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class OutboxMessageDispatcher implements MessageDispatcher {

	private final EventPublicationRepository eventPublicationRepository;

	public OutboxMessageDispatcher(EventPublicationRepository eventPublicationRepository) {
		this.eventPublicationRepository = eventPublicationRepository;
	}

	@Override
	public void dispatch(Message message) {
		eventPublicationRepository.persist(new DefaultEventPublication(message, getHeaderId(message)));
	}

	UUID getHeaderId(Message message) {
		return UUID.fromString(message.getHeaders().getOrDefault(IdGeneratorMessageDecorator.ID, UUID.randomUUID().toString()));
	}

}
