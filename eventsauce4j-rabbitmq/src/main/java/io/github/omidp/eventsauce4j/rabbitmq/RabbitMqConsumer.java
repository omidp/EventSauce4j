package io.github.omidp.eventsauce4j.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.github.omidp.eventsauce4j.api.event.Inflector;
import io.github.omidp.eventsauce4j.api.event.MetaData;
import io.github.omidp.eventsauce4j.api.message.Message;
import io.github.omidp.eventsauce4j.api.message.MessageConsumer;
import io.github.omidp.eventsauce4j.api.outbox.EventPublicationRepository;
import io.github.omidp.eventsauce4j.core.decorator.IdGeneratorMessageDecorator;
import io.github.omidp.eventsauce4j.jackson.JacksonEventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * @author Omid Pourhadi
 */
public class RabbitMqConsumer extends DefaultConsumer {

	private static final Logger log = LoggerFactory.getLogger(RabbitMqConsumer.class);

	private final Channel channel;
	private final List<MessageConsumer> messageConsumers;
	private final Inflector inflector;
	private final EventPublicationRepository eventPublicationRepository;

	public RabbitMqConsumer(Channel channel, List<MessageConsumer> messageConsumers, Inflector inflector, EventPublicationRepository eventPublicationRepository) {
		super(channel);
		this.channel = channel;
		this.messageConsumers = messageConsumers;
		this.inflector = inflector;
		this.eventPublicationRepository = eventPublicationRepository;
	}

	@Override
	public void handleDelivery(String consumerTag,
							   Envelope envelope,
							   AMQP.BasicProperties properties,
							   byte[] body) throws IOException {
		String msg = new String(body, StandardCharsets.UTF_8);
		try {
			// --- Process message ---
			log.debug(" [x] Received: " + msg);
			if (properties.getType() != null) {
				inflector.inflect(properties.getType()).ifPresentOrElse(clz -> {
					try {
						JsonMessage<?> jsonMessage = JacksonEventSerializer.JsonSerializer().readValue(msg, jsonMessageTypeReference(clz));
						for (MessageConsumer messageConsumer : messageConsumers) {
							var message = new Message(jsonMessage.event(), new MetaData(jsonMessage.metaData()));
							messageConsumer.handle(message);
							eventPublicationRepository.markAsCompleted(getHeaderId(message));
						}
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				}, () -> {
					log.debug("No inflected class found {}", properties.getType());
				});
			}

			// Ack on success
			channel.basicAck(envelope.getDeliveryTag(), false);
		} catch (Exception e) {
			log.error("RMQ Consumer Exception", e);
			// Send to DLX by rejecting (no requeue)
			channel.basicNack(envelope.getDeliveryTag(), false, false);
		}
	}

	private UUID getHeaderId(Message message) {
		return message.metaData().containsKey(IdGeneratorMessageDecorator.ID) ? UUID.fromString(message.metaData()
			.get(IdGeneratorMessageDecorator.ID).toString()) : UUID.randomUUID();
	}

	private TypeReference<JsonMessage<?>> jsonMessageTypeReference(Class<?> inflectedClass) {
		return new TypeReference<>() {
			@Override
			public Type getType() {
				return new ParameterizedType() {
					@Override
					public Type[] getActualTypeArguments() {
						return new Type[] {inflectedClass};
					}

					@Override
					public Type getRawType() {
						return JsonMessage.class;
					}

					@Override
					public Type getOwnerType() {
						return JsonMessage.class;
					}
				};
			}
		};
	}
}