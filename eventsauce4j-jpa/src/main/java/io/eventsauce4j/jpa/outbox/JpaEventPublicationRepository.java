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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.eventsauce4j.api.event.EventPublication;
import io.eventsauce4j.api.event.EventSerializer;
import io.eventsauce4j.api.event.Inflection;
import io.eventsauce4j.api.event.MetaData;
import io.eventsauce4j.api.event.Status;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.outbox.EventPublicationRepository;
import io.eventsauce4j.core.DefaultEventPublication;
import io.eventsauce4j.jackson.JacksonEventSerializer;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Omid Pourhadi
 */
@Transactional
public class JpaEventPublicationRepository implements EventPublicationRepository {

	private final EventSerializer eventSerializer;
	private final EntityManager entityManager;
	private final Supplier<Inflection> inflection;

	public JpaEventPublicationRepository(EventSerializer eventSerializer, EntityManager entityManager, Supplier<Inflection> inflection) {
		this.eventSerializer = eventSerializer;
		this.entityManager = entityManager;
		this.inflection = inflection;
	}

	@Override
	public void persist(EventPublication eventPublication) {
		JpaEventPublication entity = new JpaEventPublication(
			eventPublication.getIdentifier(),
			eventPublication.getPublicationDate(),
			eventSerializer.serialize(eventPublication.getMessage().getEvent()),
			eventPublication.getRoutingKey(),
			eventSerializer.serialize(eventPublication.getMessage().getMetaData())
		);
		entityManager.persist(entity);
	}

	@Override
	public List<EventPublication> retrieveBatch(int batchSize) {
		return entityManager.createQuery("""
				select ep from JpaEventPublication ep
				where ep.completionDate is null
				""", JpaEventPublication.class).setMaxResults(batchSize).getResultList()
			.stream().map(this::convert)
			.filter(Objects::nonNull)
			.toList();
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
		Optional<Class<?>> inflectedClass = inflection.get().getInglectedClass(eventPublication.getRoutingKey());
		if (inflectedClass.isEmpty()) {
			return null;
		}
		return new DefaultEventPublication(new Message(
			eventSerializer.deserialize(eventPublication.getSerializedEvent(), inflectedClass.get()),
			toMetaData(eventPublication.getMetaData())
		), eventPublication.getId(), eventPublication.getPublicationDate());
	}

	private MetaData toMetaData(String metadata) {
		try {
			TypeReference<Map<String, Object>> typeRef
				= new TypeReference<>() {};
			return new MetaData(JacksonEventSerializer.JsonSerializer().readValue(metadata, typeRef));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
