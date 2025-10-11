package io.github.omidp.eventsauce4j.junit;

import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageDecorator;

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
