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

package io.github.omidp.eventsauce4j.example.payment.domain.event;

import io.github.omidp.eventsauce4j.api.event.EventSerializer;
import io.github.omidp.eventsauce4j.core.event.conversion.EventUpcaster;
import io.github.omidp.eventsauce4j.core.event.conversion.Upcaster;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
@Component
public class PaymentUserCreatedEventUpcaster implements Upcaster<PaymentUserCreated> {
	private final EventSerializer eventSerializer;

	public PaymentUserCreatedEventUpcaster() {
		this.eventSerializer = new JacksonEventSerializer();
	}

	@Override
	public PaymentUserCreated deserialize(EventUpcaster eventUpcaster) {
		if (eventUpcaster.version() == 0) {
			String content = eventUpcaster.content();
			JSONObject jsonObject = new JSONObject(content);
			String[] description = jsonObject.getString("description").split(" ");

			return new PaymentUserCreated(UUID.fromString(jsonObject.getString("id")), description[0], description[1]);
		}
		return (PaymentUserCreated) eventSerializer.deserialize(eventUpcaster.content(), eventUpcaster.clz());
	}
}
