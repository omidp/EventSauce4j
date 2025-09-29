package io.eventsauce4j.example.domain.event;

import java.util.UUID;

public record OrderCompleted(UUID orderId, String description) {
}
