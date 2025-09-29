package io.eventsauce4j.example;

import io.eventsauce4j.core.annotation.Consumer;
import io.eventsauce4j.domain.event.OrderCompleted;
import io.eventsauce4j.domain.event.OrderRefunded;
import io.eventsauce4j.domain.event.OrderStarted;
import io.eventsauce4j.api.event.EventConsumer;
import io.eventsauce4j.api.event.EventDispatcher;
import io.eventsauce4j.api.message.Message;
import io.eventsauce4j.api.message.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Consumer
public class OrderConsumer implements MessageConsumer, EventConsumer<OrderRefunded> {

	private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

	private final EventDispatcher eventDispatcher;

	public OrderConsumer(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@Override
	public void handle(Message message) {
		log.info("handle order(s) ");
		if (message.getEvent() instanceof OrderStarted) {
			log.info("order started");
			eventDispatcher.dispatch(new OrderCompleted(UUID.randomUUID(), "completed"));
		}
		if (message.getEvent() instanceof OrderCompleted) {
			log.info("order completed");
			eventDispatcher.dispatch(new OrderRefunded(UUID.randomUUID(), "refunded"));
		}
	}

	@Override
	public void handle(OrderRefunded event) {
		log.info("OrderRefundEvent : " + event);
		throw new RuntimeException("Order can't be refunded.");
	}
}
