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

import com.eventsauce4j.EventMessage;
import com.eventsauce4j.event.EventConsumer;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Omid Pourhadi
 */
public class EventMessageConsumer implements ApplicationListener<EventMessage> {

	private Map<Type, EventConsumer<?>> eventConsumers = new HashMap<>();

	@Override
	public void onApplicationEvent(EventMessage event) {
		EventConsumer eventConsumer = eventConsumers.get(event.getSource().getClass());
		if (eventConsumer != null) {
			eventConsumer.handle(event.getSource());
		}
	}

	@Override
	public boolean supportsAsyncExecution() {
		return false;
	}

	public void setEventConsumers(Map<Type, EventConsumer<?>> eventConsumers) {
		this.eventConsumers = eventConsumers;
	}
}
