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

package io.github.omidp.eventsauce4j.jdbc;

import io.github.omidp.eventsauce4j.api.event.EventPublication;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.event.Status;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.it.OrderStarter;
import io.github.omidp.eventsauce4j.jdbc.pgsql.JdbcEventPublicationRepository;
import io.github.omidp.eventsauce4j.outbox.DefaultEventPublication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Omid Pourhadi
 */
@SpringJUnitConfig(initializers = {ConfigDataApplicationContextInitializer.class}, classes = {
	JdbcTestConfig.class
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class JdbcEventPublicationRepositoryIntegrationTest {

	@Autowired
	private JdbcEventPublicationRepository eventPublicationRepository;

	@Autowired
	private DataSource dataSource;

	private JdbcClient jdbcClient;

	@BeforeEach
	void setup() {
		this.jdbcClient = JdbcClient.create(dataSource);
		JdbcTestUtils.deleteFromTables(jdbcClient, "event_publication");
	}

	@Test
	void eventPublicationRepositoryCanPersist() {
		eventPublicationRepository.persist(new DefaultEventPublication(new Message(new OrderStarter(UUID.randomUUID()), MetaData.emptyInstance()), UUID.randomUUID(), Instant.now()));
		String actual = jdbcClient.sql("select status from  event_publication").query(String.class).single();
		assertEquals(Status.PROCESSING.toString(), actual);
	}

	@Test
	void eventPublicationRepositoryCanRetrieveBatch() {
		eventPublicationRepository.persist(new DefaultEventPublication(new Message(new OrderStarter(UUID.randomUUID()), MetaData.emptyInstance()), UUID.randomUUID(), Instant.now()));
		eventPublicationRepository.persist(new DefaultEventPublication(new Message(new OrderStarter(UUID.randomUUID()), MetaData.emptyInstance()), UUID.randomUUID(), Instant.now()));
		List<EventPublication> actual = eventPublicationRepository.retrieveBatch(10);
		assertEquals(2, actual.size());
	}

	@Test
	void eventPublicationRepositoryCanMarkAsCompleted() {
		var id = UUID.randomUUID();
		eventPublicationRepository.persist(new DefaultEventPublication(new Message(new OrderStarter(UUID.randomUUID()), MetaData.emptyInstance()),
			id, Instant.now()));
		eventPublicationRepository.markAsCompleted(id);
		String actual = jdbcClient.sql("select status from  event_publication where id = :id").param("id", id).query(String.class).single();
		assertEquals(Status.COMPLETED.toString(), actual);
	}

	@Test
	void eventPublicationRepositoryCanMarkAsPublished() {
		var id = UUID.randomUUID();
		eventPublicationRepository.persist(new DefaultEventPublication(new Message(new OrderStarter(UUID.randomUUID()), MetaData.emptyInstance()),
			id, Instant.now()));
		eventPublicationRepository.markAsPublished(id);
		String actual = jdbcClient.sql("select status from  event_publication where id = :id").param("id", id).query(String.class).single();
		assertEquals(Status.PUBLISHED.toString(), actual);
	}

	@Test
	void eventPublicationRepositoryCanDelete() {
		var id = UUID.randomUUID();
		eventPublicationRepository.persist(new DefaultEventPublication(new Message(new OrderStarter(UUID.randomUUID()), MetaData.emptyInstance()),
			id, Instant.now()));
		eventPublicationRepository.delete(id);
		Number actual = jdbcClient.sql("select count(*) from  event_publication").query(Number.class).single();
		assertEquals(0, actual.intValue());
	}


}
