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

package io.eventsauce4j.it;

import io.eventsauce4j.jpa.outbox.JpaEventPublication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({JpaProperties.class, DataSourceProperties.class})
public class JpaTestConfig {


	@Bean("entityManager")
	@DependsOn("entityManagerFactory")
	EntityManager entityManager(EntityManagerFactory entityManagerFactory){
		return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSourceProperties dataSourceProperties, JpaProperties jpaProperties) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource(dataSourceProperties));
		entityManagerFactoryBean.setPackagesToScan(JpaEventPublication.class.getPackageName());
		entityManagerFactoryBean.setJpaVendorAdapter(getHibernateJpaVendorAdapter(jpaProperties));
		return entityManagerFactoryBean;
	}

	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
		dataSource.setUrl(dataSourceProperties.getUrl());
		dataSource.setUsername(dataSourceProperties.getUsername());
		dataSource.setPassword(dataSourceProperties.getPassword());
		return dataSource;
	}

	@Bean
	@DependsOn("entityManagerFactory")
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private HibernateJpaVendorAdapter getHibernateJpaVendorAdapter(JpaProperties jpaProperties) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(jpaProperties.isShowSql());
		vendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
		vendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
		return vendorAdapter;
	}
}