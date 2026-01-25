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

package io.github.omidp.eventsauce4j.testcontainers.rmq;

import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * @author Omid Pourhadi
 */
public class AbstractRabbitMQContainerBaseTest {

	public static RabbitMQContainer RABBITMQ_CONTAINER;

	static {
		RABBITMQ_CONTAINER = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.7.25-management-alpine"));
		RABBITMQ_CONTAINER
			.withAdminUser("guest").withAdminPassword("guest")
			.start();
	}

}
