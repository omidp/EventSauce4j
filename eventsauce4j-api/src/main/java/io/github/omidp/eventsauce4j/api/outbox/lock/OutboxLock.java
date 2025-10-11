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

package io.github.omidp.eventsauce4j.api.outbox.lock;

/**
 * * Provides a locking mechanism for the Outbox pattern to ensure
 * * that events are processed safely in a distributed environment.
 * *
 * * <p>When multiple application instances are running, it is critical
 * * to guarantee that a given event is processed by only one instance
 * * at a time, unless the consumer is explicitly designed to be
 * * idempotent.</p>
 * *
 * * <p>Implementations of this interface may use various strategies
 * * (e.g., database row locks, distributed locks, or external systems
 * * like Redis) to coordinate exclusive access.</p>
 *
 * @author Omid Pourhadi
 */
public interface OutboxLock {

	/**
	 * Attempts to acquire the lock for event processing.
	 */
	boolean acquireLock();

	/**
	 * Releases the lock, allowing other instances to acquire it.
	 */
	void releaseLock();

}
