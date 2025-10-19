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
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * {@code EventMessageConsumer} is responsible for handling published {@link EventMessage} instances
 * and dispatching them to one or more registered {@link MessageConsumer}s.
 * <p>
 * This class listens for domain events via a Spring {@link org.springframework.transaction.event.TransactionalEventListener},
 * ensuring that the event handling occurs within the transaction boundary or after a transaction commit.
 * </p>
 *
 * @author Omid Pourhadi
 */
public class EventMessageConsumer {

	private final List<MessageConsumer> messageConsumers;

	public EventMessageConsumer(List<MessageConsumer> messageConsumers) {
		this.messageConsumers = messageConsumers;
	}

	@TransactionalEventListener(fallbackExecution = true)
	public void onHandleEvent(EventMessage event) {
		for (MessageConsumer messageConsumer : messageConsumers) {
			messageConsumer.handle(event.message());
		}
	}

}
