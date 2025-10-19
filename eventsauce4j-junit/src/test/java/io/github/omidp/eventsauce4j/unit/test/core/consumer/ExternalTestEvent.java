package io.github.omidp.eventsauce4j.core.consumer;

import io.github.omidp.eventsauce4j.api.event.ExternalEvent;

@ExternalEvent(routingKey = "payment.test")
public record ExternalTestEvent(String name) {
}