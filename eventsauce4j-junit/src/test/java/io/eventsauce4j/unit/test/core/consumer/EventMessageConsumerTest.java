package io.eventsauce4j.unit.test.core.consumer;

import io.eventsauce4j.TestEventPublicationRepository;
import io.eventsauce4j.TestMessageConsumer;
import io.eventsauce4j.api.event.MetaData;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.core.EventMessage;
import io.eventsauce4j.core.consumer.EventMessageConsumer;
import io.eventsauce4j.domain.UserCreated;
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
		this.sut = new EventMessageConsumer(List.of(messageConsumer), () -> eventPublicationRepository);
	}

	@Test
	void test() {
		var msgId = UUID.randomUUID();
		var msg = new Message(new UserCreated(), new MetaData(Map.of("id", msgId)));
		sut.onApplicationEvent(new EventMessage(msg));
		assertTrue(messageConsumer.getMessageList().contains(msg));
		assertTrue(eventPublicationRepository.isPublished());
		assertEquals(msgId, eventPublicationRepository.getId());
	}
}
