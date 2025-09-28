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

package com.eventsauce4j.decorator;

import com.eventsauce4j.message.Message;
import com.eventsauce4j.message.MessageDecorator;

import java.util.Map;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class IdGeneratorMessageDecorator implements MessageDecorator {

	public static final String ID = "id";

	@Override
	public Message decorate(Message message) {
		//TODO: implement cloud event spec
		return new Message(message.getEvent(), Map.of(ID, UUID.randomUUID().toString()));
	}
}
