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

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageDecorator;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;

/**
 * {@code SynchronousEventDispatcher} is the main component responsible for
 * dispatching domain events synchronously within the application context.
 * <p>
 * It implements the {@link EventDispatcher} interface and delegates event processing
 * to a configured {@link MessageDispatcher}, while enriching each event using a
 * {@link MessageDecorator} before dispatching. This ensures that every dispatched
 * {@link Message} contains the appropriate metadata and is processed immediately
 * within the caller's thread.
 * </p>
 *
 * <h3>Behavior</h3>
 * <ul>
 *   <li>Events are dispatched <b>synchronously</b> — the caller thread executes the dispatch logic.</li>
 *   <li>Each event is wrapped in a {@link Message} object containing optional {@link MetaData}.</li>
 *   <li>A {@link MessageDecorator} is applied before dispatch to allow enrichment
 *       (e.g., adding timestamps, correlation IDs, or tracing headers).</li>
 * </ul>
 *
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * EventDispatcher dispatcher = new SynchronousEventDispatcher(messageDispatcher, messageDecorator);
 * dispatcher.dispatch(new OrderCreatedEvent(orderId));
 * }</pre>
 *
 * @author Omid Pourhadi
 */
public class SynchronousEventDispatcher implements EventDispatcher {

	private final MessageDispatcher messageDispatcher;
	private final MessageDecorator messageDecorator;

	public SynchronousEventDispatcher(MessageDispatcher messageDispatcher, MessageDecorator messageDecorator) {
		this.messageDispatcher = messageDispatcher;
		this.messageDecorator = messageDecorator;
	}

	@Override
	public void dispatch(Object... events) {
		for (Object event : events) {
			messageDispatcher.dispatch(messageDecorator.decorate(new Message(event, MetaData.emptyInstance())));
		}
	}

	@Override
	public void dispatchWithHeaders(MetaData metaData, Object... events) {
		for (Object event : events) {
			messageDispatcher.dispatch(messageDecorator.decorate(new Message(event, metaData)));
		}
	}
}
