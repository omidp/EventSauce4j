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

package io.github.omidp.eventsauce4j.core.consumer;

import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.event.EventMessage;
import io.github.omidp.eventsauce4j.core.event.MetaDataFieldExtractorFunction;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Omid Pourhadi
 */
public class EventMessageConsumer {

	private final List<MessageConsumer> messageConsumers;
	private final Supplier<EventPublicationRepository> eventPublicationRepository;

	public EventMessageConsumer(List<MessageConsumer> messageConsumers,
								Supplier<EventPublicationRepository> eventPublicationRepository) {
		this.messageConsumers = messageConsumers;
		this.eventPublicationRepository = eventPublicationRepository;
	}

	@TransactionalEventListener(fallbackExecution = true)
	public void onHandleEvent(EventMessage event) {
		for (MessageConsumer messageConsumer : messageConsumers) {
			messageConsumer.handle(event.message());
			eventPublicationRepository.get()
				.markAsPublished(UUID.fromString(MetaDataFieldExtractorFunction.getId().apply(event.message().metaData()).get()));
		}
	}

}
