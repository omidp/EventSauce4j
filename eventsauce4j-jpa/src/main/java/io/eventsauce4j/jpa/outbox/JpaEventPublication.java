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

package io.eventsauce4j.jpa.outbox;

import io.eventsauce4j.api.event.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
@Table(name = "event_publication")
@Entity
public class JpaEventPublication {

	@Id
	private UUID id;
	@Column(name = "publication_date")
	private Instant publicationDate;
	@Column(name = "listener_id")
	private String listenerId;
	@Column(name = "serialized_event")
	private String serializedEvent;
	@Column(name = "event_type")
	private Class<?> eventType;
	@Column(name = "meta_data")
	private String metaData;
	@Column(name = "completion_date")
	protected Instant completionDate;
	@Column(name = "last_resubmission_date")
	protected Instant lastResubmissionDate;
	@Column(name = "completion_attempts")
	protected int completionAttempts;
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	protected Status status;
	@Column(name = "consumed_at")
	protected Instant consumedAt;

	private JpaEventPublication() {
	}

	public JpaEventPublication(UUID id, Instant publicationDate, String listenerId, String serializedEvent, Class<?> eventType, String metaData) {
		this.id = id;
		this.publicationDate = publicationDate;
		this.listenerId = listenerId;
		this.serializedEvent = serializedEvent;
		this.eventType = eventType;
		this.metaData = metaData;
	}

	public String getMetaData() {
		return metaData;
	}

	public UUID getId() {
		return id;
	}

	public Instant getPublicationDate() {
		return publicationDate;
	}

	public String getSerializedEvent() {
		return serializedEvent;
	}

	public Class<?> getEventType() {
		return eventType;
	}

	public Instant getCompletionDate() {
		return completionDate;
	}
}