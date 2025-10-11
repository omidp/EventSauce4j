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
