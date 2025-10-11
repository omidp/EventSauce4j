# Tutorial Creating, Dispatching, and Consuming Events in eventsauce4j

This tutorial walks you through the core workflow of **creating**, **dispatching**, and **consuming** events using **eventsauce4j**.

---

## 📦 How to Create an Event

In **eventsauce4j**, events represent changes that occurred in your domain.  
There are two main types of events:

- **Private (Internal) Events** — used *only* within the domain.
- **Public Events** — used for *inter-service communication* via messaging (e.g., RabbitMQ).

---

### 🧩 Private or Internal Events

Private or internal events are meant to be consumed **only inside the same domain or service**.  
These events typically model internal state transitions and are not published externally.

You can define an internal event in one of two ways:

#### Option 1 — Using `@Event` Annotation

The simplest way is to annotate your event with `@Event`.  
Optionally, you can specify a routing key if you use annotation-based inflection.

```java
@Event
public record UserCreated(UUID id, String description) {
}
```

#### Option 2 — Using StaticInflector

If you prefer not to annotate your events, you can define explicit mappings in your configuration using `StaticInflector`.

Example configuration snippet:

```java
@Bean
Inflector inflection() {
    return new StaticInflector(Map.of(
        "user.internal.userCreated", UserCreated.class
    ));
}
```

___

### 🌍 Public Events

**Public events** are used for **cross-service communication** (e.g., between `user-service` and `payment-service`).
They must include a **routing key**, which determines how they’re routed in the message broker (RabbitMQ).

Example:

```java
@ExternalEvent(routingKey = "payment.public.userCreated")
public record UserCreated(UUID id, String description) {
}
```

> 📝 **Note**: The routingKey ensures that other services (like payment-service) can consume and deserialize the event properly using their own inflector mappings.

___

### 🚀 How to Dispatch an Event

Once your event is defined, you can dispatch it using the `EventDispatcher`.

Inject the dispatcher into your service or aggregate and call `dispatch()`:

```java
@Service
public class UserService {

    private final EventDispatcher eventDispatcher;

    public UserService(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void createUser() {
        eventDispatcher.dispatch(
            new UserCreated(UUID.randomUUID(), "user created.")
        );
    }
}
```

### 🎧 How to Consume an Event

To handle incoming events, implement a **consumer** class and annotate it with `@Consumer`.

Consumers must implement the `MessageConsumer` interface and override the `handle(Message message)` method.

Example:

```java
@Consumer
public class UserConsumer implements MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

    @Override
    public void handle(Message message) {
        if (message.event() instanceof UserCreated uc) {
            log.info("User created: {}", uc);
            // Your business logic here
        }
    }
}
```

You can define multiple consumers, and each will receive all messages.