package io.github.omidp.eventsauce4j.unit.test.core.consumer;

import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.core.consumer.EventMessageConsumer;
import io.github.omidp.eventsauce4j.core.event.EventMessage;
import io.github.omidp.eventsauce4j.junit.TestEventPublicationRepository;
import io.github.omidp.eventsauce4j.junit.TestMessageConsumer;
import io.github.omidp.eventsauce4j.junit.domain.UserCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Omid Pourhadi
 */
public class EventMessageConsumerTest {

	private EventMessageConsumer sut;
	private TestMessageConsumer messageConsumer;
	private TestEventPublicationRepository eventPublicationRepository;

	@BeforeEach
	void setUp() {
		this.messageConsumer = new TestMessageConsumer();
		this.eventPublicationRepository = new TestEventPublicationRepository();
		this.sut = new EventMessageConsumer(
			List.of(messageConsumer),
			() -> eventPublicationRepository
		);
	}

	@Test
	void test() {
		var msgId = UUID.randomUUID();
		var msg = new Message(new UserCreated(), new MetaData(Map.of("id", msgId)));
		sut.onHandleEvent(new EventMessage(msg));
		assertTrue(messageConsumer.getMessageList().contains(msg));
		assertTrue(eventPublicationRepository.isPublished());
		assertEquals(msgId, eventPublicationRepository.getId());
	}
}
