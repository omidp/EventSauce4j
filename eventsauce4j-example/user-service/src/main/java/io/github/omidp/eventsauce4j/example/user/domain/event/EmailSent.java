package io.github.omidp.eventsauce4j.example.user.domain.event;

import java.util.UUID;

public record EmailSent(UUID id, String description) {
}
