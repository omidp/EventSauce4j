package io.github.omidp.eventsauce4j.junit;

import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omid Pourhadi
 */
public class TestMessageDispatcher implements MessageDispatcher {

	private final List<Message> messageList;

	public TestMessageDispatcher() {
		this.messageList = new ArrayList<>();
	}


	@Override
	public void dispatch(Message message) {
		messageList.add(message);
	}

	public List<Message> getMessageList() {
		return messageList;
	}
}
