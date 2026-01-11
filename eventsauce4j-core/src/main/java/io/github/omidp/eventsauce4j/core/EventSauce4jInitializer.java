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

package io.github.omidp.eventsauce4j.core;


import io.github.omidp.eventsauce4j.core.annotation.Consumer;
import io.github.omidp.eventsauce4j.core.event.conversion.EventVersioning;
import io.github.omidp.eventsauce4j.core.event.conversion.Upcaster;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Omid Pourhadi
 */
public class EventSauce4jInitializer implements BeanPostProcessor, SmartInitializingSingleton, ApplicationContextAware, PriorityOrdered, BeanFactoryAware {

	private ApplicationContext applicationContext;
	private BeanFactory beanFactory;
	private Map<Type, Upcaster> typeUpcasterMap = new HashMap<>();

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().isAnnotationPresent(Consumer.class)) {
		}
		if (bean instanceof Upcaster<?>) {
			Type[] genericInterfaces = bean.getClass().getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if (genericInterface instanceof ParameterizedType) {
					ParameterizedType paramType = (ParameterizedType) genericInterface;
					if (paramType.getRawType().getTypeName().equals(Upcaster.class.getName())) {
						Type actualType = paramType.getActualTypeArguments()[0];
						typeUpcasterMap.put(actualType, (Upcaster) bean);
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
		if(this.beanFactory instanceof ConfigurableBeanFactory configurableBeanFactory){
			configurableBeanFactory.registerSingleton(EventVersioning.class.getName() + "_EventSauce4j", new EventVersioning(typeUpcasterMap));
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
