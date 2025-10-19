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

package io.github.omidp.eventsauce4j.jpa.outbox.lock;

import io.github.omidp.eventsauce4j.api.outbox.lock.OutboxLock;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * @author Omid Pourhadi
 */
public class JpaOutboxLock implements OutboxLock {

	private static final Logger log = LoggerFactory.getLogger(JpaOutboxLock.class);

	private final EntityManager entityManager;
	private final String lockName;

	public JpaOutboxLock(EntityManager entityManager, String lockName) {
		this.entityManager = entityManager;
		this.lockName = lockName;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean acquireLock() {
		try {
			entityManager.persist(new JpaOutboxLockEntity(lockName, Instant.now()));
			return true;
		} catch (Exception ignore) {
			log.debug("outbox is locked by another process.");
		}
		return false;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void releaseLock() {
		entityManager.createQuery("delete from JpaOutboxLockEntity").executeUpdate();
	}
}
