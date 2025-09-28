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

package com.eventsauce4j.consumer;

import com.eventsauce4j.event.EventDispatcher;
import com.eventsauce4j.message.Message;
import com.eventsauce4j.message.MessageDecorator;
import com.eventsauce4j.message.MessageDispatcher;

import java.util.Collections;
import java.util.Map;

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
			messageDispatcher.dispatch(messageDecorator.decorate(new Message(event, Collections.emptyMap())));
		}
	}

	@Override
	public void dispatchWithHeaders(Map<String, String> headers, Object... events) {
		for (Object event : events) {
			messageDispatcher.dispatch(messageDecorator.decorate(new Message(event, headers)));
		}
	}
}
