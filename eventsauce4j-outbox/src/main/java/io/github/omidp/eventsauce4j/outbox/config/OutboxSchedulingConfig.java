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

package io.github.omidp.eventsauce4j.outbox.config;

import io.github.omidp.eventsauce4j.api.outbox.OutboxRelay;
import io.github.omidp.eventsauce4j.api.outbox.lock.OutboxLock;
import io.github.omidp.eventsauce4j.core.EventSauce4jCustomConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.github.omidp.eventsauce4j.core.EventSauce4jConfig.OUTBOX_LOCK;
import static io.github.omidp.eventsauce4j.core.EventSauce4jConfig.OUTBOX_RELAY;

/**
 * @author Omid Pourhadi
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
public class OutboxSchedulingConfig implements SchedulingConfigurer, ApplicationContextAware, DisposableBean {

	private ApplicationContext applicationContext;
	private volatile boolean lockAcquired;

	public Executor outboxTaskExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		OutboxRelay outboxRelay = applicationContext.getBean(OutboxRelay.class, OUTBOX_RELAY);
		OutboxLock outboxLock = applicationContext.getBean(OutboxLock.class, OUTBOX_LOCK);
		EventSauce4jCustomConfiguration eventSauce4jCustomConfiguration = applicationContext.getBean(EventSauce4jCustomConfiguration.class);
		taskRegistrar.setScheduler(outboxTaskExecutor());
		taskRegistrar.addTriggerTask(
			() -> {
				lockAcquired = outboxLock.acquireLock();
				try {
					if (lockAcquired) {
						outboxRelay.publish();
					}
				} finally {
					if (lockAcquired) {
						outboxLock.releaseLock();
					}
				}
			},
			triggerContext -> {
				if (triggerContext.lastCompletion() == null) {
					return triggerContext.getClock().instant();
				}
				return triggerContext.lastCompletion().plusSeconds(eventSauce4jCustomConfiguration.getOutboxDelayInterval());
			}
		);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void destroy() throws Exception {
		if (lockAcquired) {
			OutboxLock outboxLock = applicationContext.getBean(OutboxLock.class, OUTBOX_LOCK);
			outboxLock.releaseLock();
		}
	}
}