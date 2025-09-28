package com.eventsauce4j.domain.event;

import java.util.UUID;

public record OrderCompleted(UUID orderId, String description) {
}
