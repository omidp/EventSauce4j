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

package com.eventsauce4j.jpa.outbox.dlq;

import com.eventsauce4j.event.EventPublication;
import com.eventsauce4j.message.MessageSerializer;
import com.eventsauce4j.outbox.dlq.DeadLetterQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;

/**
 * @author Omid Pourhadi
 */
public class JpaDeadLetterQueue implements DeadLetterQueue {

	private final EntityManager entityManager;
	private final MessageSerializer messageSerializer;
	private final JsonMapper jsonMapper = new JsonMapper();


	public JpaDeadLetterQueue(EntityManager entityManager, MessageSerializer messageSerializer) {
		this.entityManager = entityManager;
		this.messageSerializer = messageSerializer;
	}

	@Override
	public void process(EventPublication eventPublication) {
		JpaEventPublicationDlq entity = new JpaEventPublicationDlq(
			eventPublication.getIdentifier(),
			eventPublication.getPublicationDate(),
			"",
			messageSerializer.serialize(eventPublication.getMessage().getEvent()),
			eventPublication.getMessage().getEvent().getClass(),
			writeValueAsString(eventPublication.getMessage().getHeaders())
		);
		entityManager.persist(entity);
	}

	private String writeValueAsString(Object object) {
		try {
			return jsonMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
