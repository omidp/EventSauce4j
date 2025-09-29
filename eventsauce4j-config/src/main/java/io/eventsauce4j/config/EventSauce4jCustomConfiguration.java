package io.eventsauce4j.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eventsauce4j")
public class EventSauce4jCustomConfiguration {

	@Value("${eventsauce4j.outboxDelayInterval:10}")
	private int outboxDelayInterval;

	@Value("${eventsauce4j.persistence:JPA}")
	private Persistence persistence;

	public Persistence getPersistence() {
		return persistence;
	}

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	public int getOutboxDelayInterval() {
		return outboxDelayInterval;
	}

	public void setOutboxDelayInterval(int outboxDelayInterval) {
		this.outboxDelayInterval = outboxDelayInterval;
	}

	enum Persistence {
		JPA, JDBC;
	}
}
