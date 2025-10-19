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

package io.github.omidp.eventsauce4j.jpa.outbox.dlq;

import io.github.omidp.eventsauce4j.api.event.Status;
import io.github.omidp.eventsauce4j.jpa.outbox.JpaEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
@Entity
@Table(name = "event_publication_dlq")
public class JpaEventPublicationDlq extends JpaEvent {

	private JpaEventPublicationDlq() {
		super();
	}

	public JpaEventPublicationDlq(UUID id, Instant publicationDate, String serializedEvent, String routingKey, String metaData, Status status) {
		super(id, publicationDate, serializedEvent, routingKey, metaData, status);
	}
}
