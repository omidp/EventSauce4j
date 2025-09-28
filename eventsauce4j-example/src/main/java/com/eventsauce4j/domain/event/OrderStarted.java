package com.eventsauce4j.domain.event;

import java.util.UUID;

public record OrderStarted(UUID orderId, String description) {
}
