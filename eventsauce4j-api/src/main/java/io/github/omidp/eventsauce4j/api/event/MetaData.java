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

package io.github.omidp.eventsauce4j.api.event;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Omid Pourhadi
 */
public class MetaData implements Map<String, Object>, Serializable {

	private static final MetaData EMPTY_META_DATA = new MetaData();
	private static final String UNSUPPORTED_OPERATION_MESSAGE = "Event meta-data is immutable.";

	private final Map<String, Object> values;

	/**
	 * Returns an empty MetaData instance.
	 *
	 * @return an empty MetaData instance
	 */
	public static MetaData emptyInstance() {
		return EMPTY_META_DATA;
	}

	private MetaData() {
		values = Collections.emptyMap();
	}

	public MetaData(Map<String, ?> items) {
		values = Collections.unmodifiableMap(new HashMap<String, Object>(items));
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return values.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
	}

	@Override
	public Set<String> keySet() {
		return values.keySet();
	}

	@Override
	public Collection<Object> values() {
		return values.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return values.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Map)) {
			return false;
		}

		Map that = (Map) o;

		return values.equals(that);
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}
}
