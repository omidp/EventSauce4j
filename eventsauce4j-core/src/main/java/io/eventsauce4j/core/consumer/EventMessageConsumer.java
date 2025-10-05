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

package io.eventsauce4j.core.consumer;

import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageConsumer;
import io.eventsauce4j.api.outbox.EventPublicationRepository;
import io.eventsauce4j.core.EventMessage;
import io.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Omid Pourhadi
 */
public class EventMessageConsumer implements ApplicationListener<EventMessage> {

	private final List<MessageConsumer> messageConsumers;
	private final Supplier<EventPublicationRepository> eventPublicationRepository;

	public EventMessageConsumer(List<MessageConsumer> messageConsumers, Supplier<EventPublicationRepository> eventPublicationRepository) {
		this.messageConsumers = messageConsumers;
		this.eventPublicationRepository = eventPublicationRepository;
	}

	@Override
	public void onApplicationEvent(EventMessage event) {
		for (MessageConsumer messageConsumer : messageConsumers) {
			messageConsumer.handle(event.getMessage());
			eventPublicationRepository.get().markAsPublished(getHeaderId(event.getMessage()));
		}
	}

	UUID getHeaderId(Message message) {
		return message.metaData().containsKey(IdGeneratorMessageDecorator.ID) ? UUID.fromString(message.metaData()
			.get(IdGeneratorMessageDecorator.ID).toString()) : UUID.randomUUID();
	}

	@Override
	public boolean supportsAsyncExecution() {
		return false;
	}

}
