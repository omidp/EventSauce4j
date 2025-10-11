package io.github.omidp.eventsauce4j.unit.test.core.consumer;

import io.github.omidp.eventsauce4j.junit.TestMessageDecorator;
import io.github.omidp.eventsauce4j.junit.TestMessageDispatcher;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.github.omidp.eventsauce4j.junit.domain.EmailSent;
import io.github.omidp.eventsauce4j.junit.domain.UserCreated;
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
