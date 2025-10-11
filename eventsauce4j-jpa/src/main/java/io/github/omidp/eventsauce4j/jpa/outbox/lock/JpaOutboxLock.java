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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * @author Omid Pourhadi
 */
@Entity
@Table(name = "outbox_lock")
public class JpaOutboxLock {

	@Id
	@Column(name = "lock_name")
	private String name;

	@Column(name = "lock_at")
	private Instant lockAt;

	private JpaOutboxLock() {
	}

	public JpaOutboxLock(String name, Instant lockAt) {
		this.name = name;
		this.lockAt = lockAt;
	}

}
