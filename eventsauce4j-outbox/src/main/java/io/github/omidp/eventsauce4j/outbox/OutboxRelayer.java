package io.github.omidp.eventsauce4j.outbox;

import io.github.omidp.eventsauce4j.api.event.EventPublication;
import io.github.omidp.eventsauce4j.api.message.MessageDispatcher;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.api.outbox.OutboxRelay;
import io.github.omidp.eventsauce4j.api.outbox.backoff.BackOffStrategy;
import io.github.omidp.eventsauce4j.api.outbox.dlq.DeadLetter;
import io.github.omidp.eventsauce4j.api.outbox.relay.RelayCommitStrategy;
import io.github.omidp.eventsauce4j.outbox.backoff.BackOffStrategyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Omid Pourhadi
 */
public class OutboxRelayer implements OutboxRelay {

	private static final Logger log = LoggerFactory.getLogger(OutboxRelayer.class);

	private final EventPublicationRepository eventPublicationRepository;
	private final MessageDispatcher messageDispatcher;
	private final BackOffStrategy backOffStrategy;
	private final RelayCommitStrategy relayCommitStrategy;
	private final DeadLetter deadLetter;
	private int tries;

	public OutboxRelayer(EventPublicationRepository eventPublicationRepository, MessageDispatcher messageDispatcher, BackOffStrategy backOffStrategy, RelayCommitStrategy relayCommitStrategy, DeadLetter deadLetter) {
		this.eventPublicationRepository = eventPublicationRepository;
		this.messageDispatcher = messageDispatcher;
		this.backOffStrategy = backOffStrategy;
		this.relayCommitStrategy = relayCommitStrategy;
		this.deadLetter = deadLetter;
		this.tries = 0;
	}

	@Override
	@Transactional
	public void publish() {
		LinkedList<EventPublication> eventPublications = new LinkedList<>(eventPublicationRepository.retrieveBatch(1000));
		List<EventPublication> publishedEvents = new ArrayList<>();
		while (!eventPublications.isEmpty()) {
			var eventMessage = eventPublications.getFirst();
			eventPublications.removeFirst();
			publishedEvents.add(eventMessage);
			execute(eventMessage);
		}
		if (!publishedEvents.isEmpty()) {
			relayCommitStrategy.commitMessage(eventPublicationRepository, publishedEvents.toArray(new EventPublication[publishedEvents.size()]));
		}
	}

	private void execute(EventPublication eventMessage) {
		try {
			tries++;
			messageDispatcher.dispatch(eventMessage.getMessage());
		} catch (Exception t) {
			//retires
			try {
				backOffStrategy.backoff(tries, t, () -> execute(eventMessage));
			} catch (BackOffStrategyException ex) {
				log.info("Unable to consume event with id {} after retries.", eventMessage.getIdentifier());
				deadLetter.process(eventMessage);
			}
		}
	}

}