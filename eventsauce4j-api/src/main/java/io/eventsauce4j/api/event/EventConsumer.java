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

package io.eventsauce4j.api.event;

/**
 * Generic contract for handling domain events in an event-driven system.
 * <p>
 * Implementations of this interface define how a specific type of event should be
 * processed when it is published by the system. This allows decoupling of event
 * producers from event consumers and supports extensible event-driven architectures.
 * </p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * public class OrderConsumer implements EventConsumer<OrderRefunded> {
 *     @Override
 *     public void handle(OrderRefunded event) {
 *         log.info("Processing refund event: " + event);
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of event this consumer can handle
 *
 *  @author Omid Pourhadi
 *
 */
public interface EventConsumer<T> {

	/**
	 * Handle the given event.
	 * <p>
	 * This method will be invoked when an event of type {@code T} is dispatched
	 * to the consumer. The implementation should contain the business logic
	 * that needs to be executed in reaction to the event.
	 * </p>
	 *
	 * @param event the event to handle, must not be {@code null}
	 */
	void handle(T event);
}
