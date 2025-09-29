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

package io.eventsauce4j.config;

import io.eventsauce4j.api.outbox.OutboxRelay;
import io.eventsauce4j.api.outbox.lock.OutboxLock;
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

/**
 * @author Omid Pourhadi
 */
@Configuration
@EnableScheduling
public class OutboxSchedulingConfig implements SchedulingConfigurer, ApplicationContextAware, DisposableBean {

	private ApplicationContext applicationContext;

	public Executor outboxTaskExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		OutboxRelay outboxRelay = applicationContext.getBean(OutboxRelay.class, "outboxRelay");
		OutboxLock outboxLock = applicationContext.getBean(OutboxLock.class, "outboxLock");
		taskRegistrar.setScheduler(outboxTaskExecutor());
		taskRegistrar.addTriggerTask(
			() -> {
				boolean lockAcquired = outboxLock.acquireLock();
				try {
					if (lockAcquired) {
						outboxRelay.publish();
					}
				} finally {
					if (lockAcquired){
						outboxLock.releaseLock();
					}
				}
			},
			triggerContext -> {
				if (triggerContext.lastCompletion() == null) {
					return triggerContext.getClock().instant();
				}
				return triggerContext.lastCompletion().plusSeconds(5);
			}
		);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void destroy() throws Exception {
	}
}