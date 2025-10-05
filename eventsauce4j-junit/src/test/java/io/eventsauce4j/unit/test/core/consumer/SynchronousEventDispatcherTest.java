package io.eventsauce4j.unit.test.core.consumer;

import io.eventsauce4j.TestMessageDecorator;
import io.eventsauce4j.TestMessageDispatcher;
import io.eventsauce4j.api.event.MetaData;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.eventsauce4j.domain.EmailSent;
import io.eventsauce4j.domain.UserCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Omid Pourhadi
 */
public class SynchronousEventDispatcherTest {
	private SynchronousEventDispatcher sut;
	private TestMessageDispatcher messageDispatcher;
	private TestMessageDecorator messageDecorator;

	@BeforeEach
	void setUp() {
		this.messageDispatcher = new TestMessageDispatcher();
		this.messageDecorator = new TestMessageDecorator();
		this.sut = new SynchronousEventDispatcher(messageDispatcher, messageDecorator);
	}

	@Test
	void testDispatch() {
		var event = new UserCreated();
		var event2 = new EmailSent(3);
		sut.dispatch(event, event2);
		assertTrue(messageDispatcher.getMessageList().contains(new Message(event, new MetaData(Map.of("id", 123456789)))));
		assertTrue(messageDispatcher.getMessageList().contains(new Message(event2, new MetaData(Map.of("id", 123456789)))));
	}
}
