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

package io.github.omidp.eventsauce4j.jpa.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.omidp.eventsauce4j.api.event.EventPublication;
import io.github.omidp.eventsauce4j.api.event.EventSerializer;
import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.event.Status;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import io.github.omidp.eventsauce4j.outbox.DefaultEventPublication;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Omid Pourhadi
 */
@Transactional
public class JpaEventPublicationRepository implements EventPublicationRepository {

	private static final Logger log = LoggerFactory.getLogger(JpaEventPublicationRepository.class);

	private final EventSerializer eventSerializer;
	private final EntityManager entityManager;
	private final Supplier<Inflector> inflection;

	public JpaEventPublicationRepository(EventSerializer eventSerializer, EntityManager entityManager, Supplier<Inflector> inflection) {
		this.eventSerializer = eventSerializer;
		this.entityManager = entityManager;
		this.inflection = inflection;
	}

	@Override
	public void persist(EventPublication eventPublication) {
		JpaEventPublication entity = new JpaEventPublication(
			eventPublication.getIdentifier(),
			eventPublication.getPublicationDate(),
			eventSerializer.serialize(eventPublication.getMessage().event()),
			eventPublication.getRoutingKey(),
			eventSerializer.serialize(eventPublication.getMessage().metaData()),
			Status.PROCESSING
		);
		entityManager.persist(entity);
	}

	@Override
	public List<EventPublication> retrieveBatch(int batchSize) {
		return entityManager.createQuery("""
				select ep from JpaEventPublication ep
				where ep.status <> :status and ep.consumedAt is null
				""", JpaEventPublication.class)
			.setParameter("status", Status.COMPLETED)
			.setMaxResults(batchSize).getResultList()
			.stream().map(this::convert)
			.toList();
	}

	@Override
	public void markAsPublished(UUID id) {
		entityManager.createQuery("""
				update JpaEventPublication ep set ep.completionDate = :now, ep.status = :status 
				where ep.id = :id
				""").setParameter("now", Clock.systemUTC().instant())
			.setParameter("id", id)
			.setParameter("status", Status.PUBLISHED)
			.executeUpdate();
	}

	@Override
	public void markAsCompleted(UUID id) {
		entityManager.createQuery("""
				update JpaEventPublication ep set ep.consumedAt = :now, ep.status = :status 
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
			eventPublication.getSerializedEvent(),
			toMetaData(eventPublication.getMetaData(), eventPublication.getRoutingKey())
		), eventPublication.getId(), eventPublication.getPublicationDate());
	}

	private MetaData toMetaData(String metadata, String routingKey) {
		try {
			TypeReference<Map<String, Object>> typeRef
				= new TypeReference<>() {};
			Map<String, Object> meta = JacksonEventSerializer.JsonSerializer().readValue(metadata, typeRef);
			meta.put("type", routingKey);
			return new MetaData(meta);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
