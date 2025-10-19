package io.github.omidp.eventsauce4j.unit.test.core.consumer;

import io.github.omidp.eventsauce4j.api.event.EventDispatcher;
import io.github.omidp.eventsauce4j.api.message.MessageDecorator;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.EventSauce4jConfig;
import io.github.omidp.eventsauce4j.core.consumer.SynchronousEventDispatcher;
import io.github.omidp.eventsauce4j.core.dispatcher.MessageDispatcherChain;
import io.github.omidp.eventsauce4j.junit.TestEventPublicationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import(EventSauce4jConfig.class)
public class TestConfig {
	@Bean
	EventMessageConsumerSpringTest.TestConsumer testConsumer() {
		return new EventMessageConsumerSpringTest.TestConsumer();
	}

	@Bean
	EventDispatcher eventDispatcher(List<MessageDispatcher> messageDispatchers, MessageDecorator messageDecorator) {
		return new SynchronousEventDispatcher(new MessageDispatcherChain(messageDispatchers), messageDecorator);
	}

	@Bean
	EventPublicationRepository eventPublicationRepository() {
		return new TestEventPublicationRepository();
	}
}