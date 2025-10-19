package io.github.omidp.eventsauce4j.jackson;

/**
 * @author Omid Pourhadi
 */
public class JsonProcessingException extends RuntimeException{

	public JsonProcessingException() {
	}

	public JsonProcessingException(String message) {
		super(message);
	}

	public JsonProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonProcessingException(Throwable cause) {
		super(cause);
	}

	public JsonProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
