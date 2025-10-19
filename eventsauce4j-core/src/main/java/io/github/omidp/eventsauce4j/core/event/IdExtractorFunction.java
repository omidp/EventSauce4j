package io.github.omidp.eventsauce4j.outbox;

import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Omid Pourhadi
 */
public interface IdExtractorFunction extends Function<MetaData, UUID> {


	static IdExtractorFunction getId() {
		return metaData -> metaData.containsKey(IdGeneratorMessageDecorator.ID) ? UUID.fromString(metaData.get(IdGeneratorMessageDecorator.ID)
			.toString()) : UUID.randomUUID();
	}
}
