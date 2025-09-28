package com.eventsauce4j.example;

import com.eventsauce4j.domain.event.OrderRefunded;
import com.eventsauce4j.event.EventDispatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class EventEmitterController {

	private final EventDispatcher eventDispatcher;

	public EventEmitterController(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	@GetMapping("/emit")
	public String ok() {
		eventDispatcher.dispatch(new OrderRefunded(UUID.randomUUID(), "order-refund"));
		return "ok";
	}

}
