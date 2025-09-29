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


import io.eventsauce4j.core.annotation.Consumer;
import io.eventsauce4j.core.annotation.Externalized;
import io.eventsauce4j.core.consumer.EventMessageConsumer;
import io.eventsauce4j.core.dispatcher.SynchronousMessageDispatcher;
import io.eventsauce4j.api.event.EventConsumer;
import io.eventsauce4j.api.message.MessageConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omid Pourhadi
 */
public class EventSauce4jInitializer implements BeanPostProcessor, SmartInitializingSingleton, ApplicationContextAware, PriorityOrdered {

	private List<MessageConsumer> consumers = new ArrayList<>();
	private Map<Type, EventConsumer<?>> eventConsumers = new HashMap<>();
	private ApplicationContext applicationContext;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean.getClass().isAnnotationPresent(Externalized.class)){
			//TODO: events need routing
		}
		if (bean.getClass().isAnnotationPresent(Consumer.class) || bean instanceof MessageConsumer) {
			consumers.add((MessageConsumer) bean);
		}
		if (bean instanceof EventConsumer<?>) {
			Type[] genericInterfaces = bean.getClass().getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if (genericInterface instanceof ParameterizedType) {
					ParameterizedType paramType = (ParameterizedType) genericInterface;
					if (paramType.getRawType().getTypeName().equals(EventConsumer.class.getName())) {
						Type actualType = paramType.getActualTypeArguments()[0];
						eventConsumers.put(actualType, (EventConsumer<?>) bean);
					}
				}
			}
		}
		return bean;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterSingletonsInstantiated() {
		SynchronousMessageDispatcher synchronousMessageDispatcher = applicationContext.getBean(EventSauce4jConfig.SYNCHRONOUS_MESSAGE_DISPATCHER_NAME, SynchronousMessageDispatcher.class);
		synchronousMessageDispatcher.setMessageConsumers(consumers);
		//
		EventMessageConsumer eventMessageConsumer= applicationContext.getBean(
			EventSauce4jConfig.EVENT_MESSAGE_CONSUMER,
			EventMessageConsumer.class);
		eventMessageConsumer.setEventConsumers(eventConsumers);
	}
}
