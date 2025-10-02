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

import io.eventsauce4j.api.outbox.Jitter;
import io.eventsauce4j.api.outbox.Sleeper;
import io.eventsauce4j.api.outbox.backoff.BackOffStrategy;

/**
 * @author Omid Pourhadi
 */
public class ExponentialBackOffStrategy implements BackOffStrategy {

	private final int maxTries;
	private final long initialDelayMs;
	private final double base;
	private final long maxDelayMs;
	private final Jitter jitter;
	private final Sleeper sleeper;

	public ExponentialBackOffStrategy(int maxTries, long initialDelayMs, double base, long maxDelayMs, Jitter jitter, Sleeper sleeper) {
		this.maxTries = maxTries;
		this.initialDelayMs = initialDelayMs;
		this.base = base;
		this.maxDelayMs = maxDelayMs;
		this.jitter = jitter;
		this.sleeper = sleeper;
	}

	@Override
	public void backoff(int retries, Throwable t, Runnable action) {
		if (maxTries != -1 && retries > maxTries) {
			throw new BackOffStrategyException(t);
		}

		long delay = Math.round(initialDelayMs * Math.pow(base, retries - 1));
		delay = Math.min(maxDelayMs, delay);
		delay = jitter.jitter(delay);

		try {
			sleeper.sleep(delay);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new BackOffStrategyException(ie); // propagate interruption
		}
		action.run();
	}
}
