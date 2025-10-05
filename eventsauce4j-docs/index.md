# Welcome to EventSauce4j

`eventsauce4j` is a lightweight event sourcing toolkit for **Java + Spring Framework**, inspired by [EventSauce (PHP)](https://eventsauce.io/).
It provides clean building blocks for domain events, message repositories, dispatching, outbox, with a persistence module.

> If you know EventSauce in PHP, you should feel at home: the vocabulary, responsibilities, and flow are intentionally adapted to Java idioms.

___

## Why EventSauce4j?

Event sourcing is a powerful architectural pattern, but many existing libraries are large, opinionated, or hard to integrate with.  
The goals of eventsauce4j are:

- Minimal footprint, easy to embed in Spring / Spring Boot
- Pluggable persistence / transport / serialization
- A pragmatic event sourcing library for Spring framework with a focus on developer experience.

___

## Modules

| Module | Description |
|---------|-------------|
| **eventsauce4j-core** | Synchronous implementation of the message dispatcher and core event sourcing components. |
| **eventsauce4j-jpa** | JPA-based implementation of the Outbox pattern for reliable event persistence and publication. |
| **eventsauce4j-rabbitmq** | RabbitMQ producer and consumer support for inter-service event communication. |
| **eventsauce4j-jackson** | JSON event serialization and deserialization using Jackson. |

___

# Core Concepts

This section introduces the fundamental building blocks of **eventsauce4j** and explains how they interact to support event-sourced applications in Java and Spring.

## 📨 Message Decoration

EventSauce4j allows message decoration, meaning you can attach additional metadata (headers) to your messages.
This is useful for adding contextual information before a message is persisted or dispatched, such as correlation IDs, tenant identifiers, or trace information.

___

## ⚡ Event Dispatcher

Events are a central concept in event sourcing, but they’re also valuable beyond it.
An event dispatcher decouples systems by propagating events to subscribers, enabling clean separation between write and read models or between microservices.

In eventsauce4j, the event dispatcher ensures that domain events flow seamlessly from your aggregates to handlers or external systems.

___

## 🧭 Message Dispatcher

The message dispatcher is responsible for sending messages to registered `MessageConsumers`.
It defines how events are actually delivered synchronously or asynchronously depending on the module in use.

**Custom Dispatchers**

You can implement your own message dispatcher by implementing the `MessageDispatcher` interface.

___

## 🔤 Inflector

An **Inflector** converts event class names into string identifiers when storing events.
When reconstructing events, it performs the reverse: mapping the stored string back to the correct fully-qualified class name.

This mechanism supports cross-service communication and event versioning.

**How It Works**

The Inflector composes your event type mappings using several strategies:

- **ExternalInflector** — maps external event packages
- **AnnotationInflector** — maps annotated event classes
- **StaticInflector** — defines explicit key-to-class mappings

**Example: Cross-Service Event Mapping**

When dispatching a `UserCreated` event from **user-service** to **payment-service** via `RabbitMqMessageDispatcher`,
the consumer service must know how to deserialize it to its local event type.
You can define this mapping as follows:

```java
new StaticInflector(Map.of(
  "payment.public.userCreated", PaymentUserCreated.class
));
```

___

## 🧩 Message Serializer

The message serializer is responsible for converting messages to and from serialized formats for persistence or transport.
When implementing a custom message repository, you’ll typically use this interface.

By default, eventsauce4j provides an jackson-based serializer, but you can easily implement your own strategy (e.g., JSON, Avro, Protobuf) to fit your infrastructure.

___

## 🗃️ Outbox

The **Outbox pattern** ensures that event persistence and dispatching occur atomically, either both succeed or both fail.

**Why It Matters**

Without an outbox, persisting domain changes and dispatching events involve two separate network operations.
If one fails, inconsistencies can occur.

The **transactional outbox** buffers events in a dedicated database table before dispatching them asynchronously.
Although this adds slight latency, it guarantees **at-least-once delivery** and ensures that only persisted events are published to consumers.

___

## 🔒 Outbox Lock

In horizontally scaled Spring Boot applications (multiple instances of the same service), it’s crucial to prevent duplicate event consumption.

**EventSauce4j** uses an outbox lock mechanism so that only one instance at a time processes pending events from the outbox table.
This ensures consistent, non-duplicated event delivery across your service cluster.