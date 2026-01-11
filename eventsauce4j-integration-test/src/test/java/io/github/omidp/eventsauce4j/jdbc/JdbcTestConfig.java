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

import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import io.github.omidp.eventsauce4j.jdbc.pgsql.JdbcEventPublicationRepository;
import io.github.omidp.eventsauce4j.tc.AbstractContainerBaseTest;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Omid Pourhadi
 */
@Configuration(proxyBeanMethods = false)
public class JdbcTestConfig {

	@Bean EventPublicationRepository  eventPublicationRepository(DataSource dataSource) {
		return new JdbcEventPublicationRepository(dataSource, new JacksonEventSerializer());
	}

	@Bean DataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource) {
		var setting = new DatabaseInitializationSettings();
		setting.setSchemaLocations(List.of("classpath:schema.sql"));
		setting.setMode(DatabaseInitializationMode.ALWAYS);
		return new DataSourceScriptDatabaseInitializer(dataSource, setting);
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(AbstractContainerBaseTest.POSTGRESQL_CONTAINER.getDriverClassName());
		dataSource.setUrl(AbstractContainerBaseTest.POSTGRESQL_CONTAINER.getJdbcUrl());
		dataSource.setUsername(AbstractContainerBaseTest.POSTGRESQL_CONTAINER.getUsername());
		dataSource.setPassword(AbstractContainerBaseTest.POSTGRESQL_CONTAINER.getPassword());
		return dataSource;
	}

}
