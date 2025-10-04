package io.eventsauce4j.example.domain.event;

import java.util.UUID;

public record EmailSent(UUID id, String description) {
}
