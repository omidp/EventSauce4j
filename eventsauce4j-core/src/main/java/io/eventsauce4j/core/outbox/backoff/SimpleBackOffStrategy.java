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

package io.eventsauce4j.core.outbox.backoff;

import io.eventsauce4j.api.outbox.Sleeper;
import io.eventsauce4j.api.outbox.backoff.BackOffStrategy;

import java.time.Duration;

/**
 * @author Omid Pourhadi
 */
public class SimpleBackOffStrategy implements BackOffStrategy {

	private final int maxTries;
	private final Duration delay;
	private final Sleeper sleeper;

	public SimpleBackOffStrategy(int maxTries, Duration delay) {
		this.maxTries = maxTries;
		this.delay = delay;
		this.sleeper = Sleeper.create();
	}

	@Override
	public void backoff(int retries, Throwable t, Runnable action) {
		if (maxTries != -1 && retries > maxTries) {
			throw new BackOffStrategyException(t);
		}

		try {
			sleeper.sleep(delay.toMillis());
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new BackOffStrategyException(ie); // propagate interruption
		}
		action.run();
	}
}
