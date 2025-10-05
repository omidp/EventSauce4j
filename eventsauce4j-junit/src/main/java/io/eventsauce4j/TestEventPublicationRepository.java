package io.eventsauce4j;

import io.eventsauce4j.api.event.EventPublication;
import io.eventsauce4j.api.outbox.EventPublicationRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class TestEventPublicationRepository implements EventPublicationRepository {

	private boolean published;
	private UUID id;

	@Override
	public void persist(EventPublication eventPublication) {

	}

	@Override
	public List<EventPublication> retrieveBatch(int batchSize) {
		return null;
	}

	@Override
	public void markAsCompleted(UUID id) {

	}

	@Override
	public void markAsPublished(UUID id) {
		this.published = true;
		this.id = id;
	}

	@Override
	public void delete(UUID identifier) {

	}

	@Override
	public void deleteAll() {

	}

	public boolean isPublished() {
		return published;
	}

	public UUID getId() {
		return id;
	}
}
