package io.github.omidp.eventsauce4j.core.event;

import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Omid Pourhadi
 */
public interface MetaDataFieldExtractorFunction extends Function<MetaData, Optional<String>> {

	String ROUTING_KEY = "type";

	static MetaDataFieldExtractorFunction getId() {
		return metaData -> metaData.containsKey(IdGeneratorMessageDecorator.ID) ?
			Optional.of(metaData.get(IdGeneratorMessageDecorator.ID).toString())
			: Optional.of(UUID.randomUUID().toString());
	}

	static MetaDataFieldExtractorFunction getRoutingKey() {
		return metaData -> metaData.containsKey(ROUTING_KEY) ? Optional.of(metaData.get(ROUTING_KEY).toString())
			: Optional.empty();
	}
}
