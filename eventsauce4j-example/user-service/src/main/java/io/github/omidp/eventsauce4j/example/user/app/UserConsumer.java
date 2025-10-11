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

package io.github.omidp.eventsauce4j.example.user.app;


import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.core.annotation.Consumer;
import io.github.omidp.eventsauce4j.example.user.domain.event.EmailSent;
import io.github.omidp.eventsauce4j.example.user.domain.event.UserCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Consumer
public class UserConsumer implements MessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

	private final EventDispatcher eventDispatcher;

	public UserConsumer(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public void handle(Message message) {
		log.info("UserConsumer#handle message.");
		if (message.event() instanceof UserCreated uc) {
			log.info("user created : " + uc);
			eventDispatcher.dispatch(new EmailSent(UUID.randomUUID(), "email sent"));
			eventDispatcher.dispatch(new io.github.omidp.eventsauce4j.example.user.domain.event.external.UserCreated(uc.id(), uc.description()));
		}
		if (message.event() instanceof EmailSent es) {
			log.info("email sent : " + es);
		}
	}

}
