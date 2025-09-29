package io.eventsauce4j.domain.event;

import java.util.UUID;

public record OrderRefunded(UUID orderId, String description) {
}
