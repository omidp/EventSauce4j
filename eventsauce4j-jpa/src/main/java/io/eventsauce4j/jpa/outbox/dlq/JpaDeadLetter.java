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

package io.eventsauce4j.jpa.outbox.dlq;

import io.eventsauce4j.api.event.EventPublication;
import io.eventsauce4j.api.message.MessageSerializer;
import io.eventsauce4j.api.outbox.dlq.DeadLetter;
import jakarta.persistence.EntityManager;

/**
 * @author Omid Pourhadi
 */
public class JpaDeadLetter implements DeadLetter {

	private final EntityManager entityManager;
	private final MessageSerializer messageSerializer;

	public JpaDeadLetter(EntityManager entityManager, MessageSerializer messageSerializer) {
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
			messageSerializer.serialize(eventPublication.getMessage().getMetaData())
		);
		entityManager.persist(entity);
	}
}
