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

package io.eventsauce4j.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.eventsauce4j.api.event.EventSerializer;

/**
 * @author Omid Pourhadi
 */
public class JacksonEventSerializer implements EventSerializer {
	private static JsonMapper jsonMapper = JsonMapper.builder().build();

	@Override
	public String serialize(Object event) {
		try {
			return jsonMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T deserialize(String payload, Class<T> clz) {
		try {
			return jsonMapper.readValue(payload, clz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static JsonMapper JsonSerializer() {
		return jsonMapper;
	}

}
