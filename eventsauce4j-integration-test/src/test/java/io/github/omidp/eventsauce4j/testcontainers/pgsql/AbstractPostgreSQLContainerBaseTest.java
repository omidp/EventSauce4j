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

package io.github.omidp.eventsauce4j.testcontainers.pgsql;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractPostgreSQLContainerBaseTest {

	public static final PostgreSQLContainer POSTGRESQL_CONTAINER;

	static {
		PostgreSQLProperties properties = new PostgreSQLProperties();
		POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(ContainerUtils.getDockerImageName(properties))
			.withUsername(properties.getUser())
			.withPassword(properties.getPassword())
			.withDatabaseName(properties.getDatabase())
			.withInitScript(properties.getInitScriptPath());
		POSTGRESQL_CONTAINER.start();
	}

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
	}
}