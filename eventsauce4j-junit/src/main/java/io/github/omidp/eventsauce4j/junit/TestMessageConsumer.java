package io.github.omidp.eventsauce4j.junit;

import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omid Pourhadi
 */
public class TestMessageConsumer implements MessageConsumer {

	private final List<Message> messageList;

	public TestMessageConsumer() {
		this.messageList = new ArrayList<>();
	}


	@Override
	public void handle(Message message) {
		messageList.add(message);
	}

	public List<Message> getMessageList() {
		return messageList;
	}
}
