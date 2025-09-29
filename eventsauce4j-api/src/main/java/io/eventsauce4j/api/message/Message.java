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

package io.eventsauce4j.api.message;

import java.util.Map;

/**
 * @author Omid Pourhadi
 */
public class Message {

	private Object event;
	private Map<String, String> headers;

	public Message(Object event, Map<String, String> headers) {
		this.event = event;
		this.headers = headers;
	}

	public Object getEvent() {
		return event;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
