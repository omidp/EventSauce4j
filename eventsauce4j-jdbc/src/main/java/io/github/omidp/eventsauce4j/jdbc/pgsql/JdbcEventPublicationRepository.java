
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

package io.github.omidp.eventsauce4j.jdbc.pgsql;

import io.github.omidp.eventsauce4j.api.event.EventPublication;
import io.github.omidp.eventsauce4j.api.event.EventSerializer;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.event.Status;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.event.MetaDataFieldExtractorFunction;
import io.github.omidp.eventsauce4j.outbox.DefaultEventPublication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
@Transactional
public class JdbcEventPublicationRepository implements EventPublicationRepository {

	private static final Logger log = LoggerFactory.getLogger(JdbcEventPublicationRepository.class);

	private static final String INSERT = """
		INSERT INTO event_publication
		(id, completion_attempts, completion_date, consumed_at, last_resubmission_date, meta_data, 
		 publication_date, serialized_event, status, routing_key, created_at)
		VALUES(:id, 0, null, null, null, :metaData, :publicationDate, :serializedEvent, :status, :routingKey, :createdAt);		
		""";

	private final JdbcClient jdbcClient;
	private final EventSerializer eventSerializer;

	public JdbcEventPublicationRepository(DataSource dataSource, EventSerializer eventSerializer) {
		this.jdbcClient = JdbcClient.create(dataSource);
		this.eventSerializer = eventSerializer;
	}

	@Override
	public void persist(EventPublication eventPublication) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", eventPublication.getIdentifier());
		params.put("publicationDate", Timestamp.from(eventPublication.getPublicationDate()));
		params.put("serializedEvent", eventSerializer.serialize(eventPublication.getMessage().event()));
		params.put("routingKey", eventPublication.getRoutingKey());
		params.put("metaData", eventSerializer.serialize(eventPublication.getMessage().metaData()));
		params.put("status", Status.PROCESSING.toString());
		params.put("createdAt", Timestamp.from(Instant.now()));
		jdbcClient.sql(INSERT).params(params).update();
	}

	@Override
	public List<EventPublication> retrieveBatch(int batchSize) {
		return jdbcClient.sql("""
				select * from event_publication ep
				where ep.status <> :status and ep.consumed_at is null
				""").param("status", Status.COMPLETED.toString())
			.query(new EventPublicationRowMapper())
			.list();
	}

	@Override
	public void markAsCompleted(UUID id) {
		jdbcClient.sql("""
				update event_publication set status = :status, consumed_at = :consumedAt where id = :id
				""")
			.param("status", Status.COMPLETED.toString())
			.param("id", id)
			.param("consumedAt", Timestamp.from(Instant.now()))
			.update();
	}

	@Override
	public void markAsPublished(UUID id) {
		jdbcClient.sql("""
				update event_publication set status = :status where id = :id
				""")
			.param("status", Status.PUBLISHED.toString())
			.param("id", id)
			.update();
	}

	@Override
	public void delete(UUID identifier) {
		jdbcClient.sql("""
				delete from event_publication where id = :id
				""")
			.param("id", identifier)
			.update();
	}

	@Override
	public void deleteAll() {
		jdbcClient.sql("""
				delete from event_publication where 1 = 1
				""")
			.update();
	}

	private class EventPublicationRowMapper implements RowMapper<EventPublication> {

		@Override
		public EventPublication mapRow(ResultSet rs, int rowNum) throws SQLException {
			Map<String, Object> headers = eventSerializer.deserialize(rs.getString("meta_data"), Map.class);
			headers.put(MetaDataFieldExtractorFunction.ROUTING_KEY, rs.getString("routing_key"));
			var message = new Message(rs.getString("serialized_event"), new MetaData(headers));
			return new DefaultEventPublication(message, rs.getObject("id", UUID.class), rs.getTimestamp("publication_date").toInstant());
		}

	}

}
