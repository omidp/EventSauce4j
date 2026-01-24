package io.github.omidp.eventsauce4j.unit.test.eventserializer;

import io.github.omidp.eventsauce4j.api.event.EventSerializer;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Omid Pourhadi
 */
class JacksonEventSerializerTest {

	private EventSerializer eventSerializer;

	@BeforeEach
	void setUp(){
		eventSerializer = new JacksonEventSerializer();
	}

	@Test
	void testDeserialize(){
		String payload = """
			{\"id\":\"8d6580f8-9113-423b-bcd0-829bf97c414e\",\"name\":\"user\"}
			""";
		UserCreated actual = eventSerializer.deserialize(payload, UserCreated.class);
		assertEquals(new UserCreated(UUID.fromString("8d6580f8-9113-423b-bcd0-829bf97c414e"), "user"), actual);
	}

}