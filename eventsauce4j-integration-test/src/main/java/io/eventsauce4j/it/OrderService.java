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

package io.eventsauce4j.it;

import io.eventsauce4j.api.event.EventDispatcher;
import io.eventsauce4j.core.annotation.Consumer;

/**
 * @author Omid Pourhadi
 */
@Consumer
public class OrderService {

	private final EventDispatcher eventDispatcher;

	public OrderService(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public void submitOrder(Order order) {
		eventDispatcher.dispatch(new OrderStarter(order.id()));
	}
}
