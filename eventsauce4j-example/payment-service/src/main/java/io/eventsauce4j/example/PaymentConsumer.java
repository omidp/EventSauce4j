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

package io.eventsauce4j.example;

import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageConsumer;
import io.eventsauce4j.core.annotation.Consumer;
import io.eventsauce4j.example.domain.event.BonusAcquired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Consumer
public class PaymentConsumer implements MessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);


	@Override
	public void handle(Message message) {
		log.info("handle message(s) ");
		if (message.event() instanceof BonusAcquired ba) {
			log.info("BonusAcquired : " + ba);
		}
	}


}
