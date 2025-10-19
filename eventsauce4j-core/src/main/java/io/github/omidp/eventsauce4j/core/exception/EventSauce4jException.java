package io.github.omidp.eventsauce4j.core.exception;

/**
 * @author Omid Pourhadi
 */
public class EventSauce4jException extends RuntimeException {

	public EventSauce4jException() {
	}

	public EventSauce4jException(String message) {
		super(message);
	}

	public EventSauce4jException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventSauce4jException(Throwable cause) {
		super(cause);
	}

	public EventSauce4jException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
