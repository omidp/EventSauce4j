package io.eventsauce4j;

import io.eventsauce4j.api.event.MetaData;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageDecorator;

import java.util.Map;

/**
 * @author Omid Pourhadi
 */
public class TestMessageDecorator implements MessageDecorator {
	@Override
	public Message decorate(Message message) {
		return new Message(message.event(), new MetaData(Map.of("id", 123456789)));
	}
}
