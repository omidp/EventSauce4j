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

package com.eventsauce4j.jpa.outbox;

import com.eventsauce4j.DefaultEventPublication;
import com.eventsauce4j.event.EventPublication;
import com.eventsauce4j.event.Status;
import com.eventsauce4j.message.Message;
import com.eventsauce4j.message.MessageConverter;
import com.eventsauce4j.outbox.EventPublicationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Omid Pourhadi
 */
@Transactional
public class JpaEventPublicationRepository implements EventPublicationRepository {

	private final MessageConverter messageConverter;
	private final EntityManager entityManager;
	private final JsonMapper jsonMapper = new JsonMapper();

	public JpaEventPublicationRepository(MessageConverter messageConverter, EntityManager entityManager) {
		this.messageConverter = messageConverter;
		this.entityManager = entityManager;
	}

	@Override
	public void persist(EventPublication eventPublication) {
		JpaEventPublication entity = new JpaEventPublication(
			eventPublication.getIdentifier(),
			eventPublication.getPublicationDate(),
			"",
			messageConverter.serialize(eventPublication.getMessage().getEvent()),
			eventPublication.getMessage().getEvent().getClass(),
			writeValueAsString(eventPublication.getMessage().getHeaders())
		);
		entityManager.persist(entity);
	}

	@Override
	public List<EventPublication> retrieveBatch(int batchSize) {
		return entityManager.createQuery("""
				select ep from JpaEventPublication ep
				where ep.completionDate is null
				""", JpaEventPublication.class).setMaxResults(batchSize).getResultList()
			.stream().map(this::convert).collect(Collectors.toList());
	}

	@Override
	public void markAsComplete(UUID id) {
		entityManager.createQuery("""
				update JpaEventPublication ep set ep.completionDate = :now, ep.status = :status 
				where ep.id = :id
				""").setParameter("now", Clock.systemUTC().instant())
			.setParameter("id", id)
			.setParameter("status", Status.COMPLETED)
			.executeUpdate();
	}

	@Override
	public void delete(UUID identifier) {
		entityManager.createQuery("""
						delete from JpaEventPublication ep where ep.id = :id
			""").setParameter("id", identifier).executeUpdate();
	}

	@Override
	public void deleteAll() {
		entityManager.createQuery("""
						delete from JpaEventPublication
			""").executeUpdate();
	}

	private EventPublication convert(JpaEventPublication eventPublication) {
		return new DefaultEventPublication(new Message(
			messageConverter.deserialize(eventPublication.getSerializedEvent(), eventPublication.getEventType()),
			toHeaders(eventPublication.getHeaders())
		), eventPublication.getId());
	}

	private String writeValueAsString(Object object) {
		try {
			return jsonMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private HashMap<String, String> toHeaders(String headers) {
		try {
			TypeReference<HashMap<String, String>> typeRef
				= new TypeReference<>() {};
			return jsonMapper.readValue(headers, typeRef);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
