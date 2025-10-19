package io.github.omidp.eventsauce4j.core.consumer;

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Omid Pourhadi
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class EventMessageConsumerTest {

	@Autowired
	private EventDispatcher eventDispatcher;
	@Autowired
	private TestConsumer testConsumer;

	@AfterEach
	void tearDown() {
		testConsumer.clear();
	}

	@Test
	void testEventConsumer() {
		var event = new TestEvent("test");
		eventDispatcher.dispatch(event);
		assertEquals(event, testConsumer.getMessages().iterator().next().event());
	}

	public static class TestConsumer implements MessageConsumer {

		private List<Message> messages = new ArrayList<>();

		@Override
		public void handle(Message message) {
			if (message.event() instanceof TestEvent) {
				messages.add(message);
			}
		}

		public void clear() {
			messages.clear();
		}

		public List<Message> getMessages() {
			return messages;
		}
	}


}