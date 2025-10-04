package io.eventsauce4j.example.domain.event;

import java.util.UUID;

public record UserCreated(UUID id, String description) {
}
