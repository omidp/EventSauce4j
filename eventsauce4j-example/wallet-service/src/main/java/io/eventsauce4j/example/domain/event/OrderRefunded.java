package io.eventsauce4j.example.domain.event;

import java.util.UUID;

public record OrderRefunded(UUID orderId, String description) {
}
