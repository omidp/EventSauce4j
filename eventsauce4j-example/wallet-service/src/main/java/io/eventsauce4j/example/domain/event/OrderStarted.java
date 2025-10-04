package io.eventsauce4j.example.domain.event;

import java.util.UUID;

public record OrderStarted(UUID orderId, String description) {
}
