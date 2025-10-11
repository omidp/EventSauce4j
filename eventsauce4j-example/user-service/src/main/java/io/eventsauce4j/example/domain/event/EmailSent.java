package io.github.omidp.example.domain.event;

import java.util.UUID;

public record EmailSent(UUID id, String description) {
}
