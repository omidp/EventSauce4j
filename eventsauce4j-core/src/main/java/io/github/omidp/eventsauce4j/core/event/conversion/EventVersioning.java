/*
 * Copyright 2024–2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.omidp.eventsauce4j.core.event.conversion;

import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.core.event.MetaDataFieldExtractorFunction;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author Omid Pourhadi
 */
public class EventVersioning {
	private final Map<Type, Upcaster> typeUpcasterMap;

	public EventVersioning(Map<Type, Upcaster> typeUpcasterMap) {
		this.typeUpcasterMap = typeUpcasterMap;
	}

	public Object getEventAsObject(String content, Class<?> clz, MetaData metaData, BiFunction<String, Class<?>, Object> eventSerializer) {
		if (MetaDataFieldExtractorFunction.getVersion().apply(metaData).isPresent()) {
			int version = Integer.parseInt(MetaDataFieldExtractorFunction.getVersion().apply(metaData).get());
			Revision revision = clz.getAnnotation(Revision.class);
			if (revision != null && version != revision.value()) {
				return getUpcaster(clz)
					.orElseThrow(() -> new ConversionException("Upcaster not found"))
					.deserialize(new EventUpcaster(content, clz, version, metaData));
			}
		}
		return eventSerializer.apply(content, clz);
	}

	private Optional<Upcaster> getUpcaster(Class<?> clz) {
		return Optional.ofNullable(typeUpcasterMap.get(clz));
	}

}
