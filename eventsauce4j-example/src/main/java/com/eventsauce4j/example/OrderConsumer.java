package com.eventsauce4j.example;

import com.eventsauce4j.annotation.Consumer;
import com.eventsauce4j.domain.event.OrderCompleted;
import com.eventsauce4j.domain.event.OrderRefunded;
import com.eventsauce4j.domain.event.OrderStarted;
import com.eventsauce4j.event.EventConsumer;
import com.eventsauce4j.event.EventDispatcher;
import com.eventsauce4j.message.Message;
import com.eventsauce4j.message.MessageConsumer;
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
