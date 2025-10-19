package io.github.omidp.eventsauce4j.unit.test.eventserializer;

import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public record UserCreated(UUID id, String name) {
}
