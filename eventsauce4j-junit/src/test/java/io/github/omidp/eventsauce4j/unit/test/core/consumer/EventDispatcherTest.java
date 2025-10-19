package io.github.omidp.eventsauce4j.core.consumer;

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageDecorator;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

/**
 * @author Omid Pourhadi
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class EventDispatcherTest {

	@Autowired
	private EventDispatcher eventDispatcher;
	@MockitoBean
	private MessageDispatcher messageDispatcher;
	@MockitoBean
	private MessageDecorator messageDecorator;


	@Test
	void testEventDispatcher() {
		var event = new TestEvent("test");
		var event2 = new TestEvent("test1");
		eventDispatcher.dispatch(event, event2);
		verify(messageDecorator).decorate(new Message(event, MetaData.emptyInstance()));
		verify(messageDecorator).decorate(new Message(event2, MetaData.emptyInstance()));
	}

	@Test
	void testEventDispatcherWithHeaders() {
		var event = new TestEvent("test");
		var event2 = new TestEvent("test1");
		eventDispatcher.dispatchWithHeaders(new MetaData(Map.of("event-type", "cloud")), event, event2);
		verify(messageDecorator).decorate(new Message(event, new MetaData(Map.of("event-type", "cloud"))));
		verify(messageDecorator).decorate(new Message(event2, new MetaData(Map.of("event-type", "cloud"))));
	}


}