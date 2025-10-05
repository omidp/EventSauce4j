# Welcome to EventSauce4j

`eventsauce4j` is a lightweight event sourcing toolkit for **Java + Spring Framework**, inspired by [EventSauce (PHP)](https://eventsauce.io/).
It provides clean building blocks for domain events, message repositories, dispatching, outbox, with a persistence module.

> If you know EventSauce in PHP, you should feel at home: the vocabulary, responsibilities, and flow are intentionally adapted to Java idioms.


## Why EventSauce4j?

Event sourcing is a powerful architectural pattern, but many existing libraries are large, opinionated, or hard to integrate with.  
The goals of eventsauce4j are:

- Minimal footprint, easy to embed in Spring / Spring Boot
- Pluggable persistence / transport / serialization
- A pragmatic event sourcing library for Spring framework with a focus on developer experience.


## Modules

| Module | Description |
|---------|-------------|
| **eventsauce4j-core** | Synchronous implementation of the message dispatcher and core event sourcing components. |
| **eventsauce4j-jpa** | JPA-based implementation of the Outbox pattern for reliable event persistence and publication. |
| **eventsauce4j-rabbitmq** | RabbitMQ producer and consumer support for inter-service event communication. |
| **eventsauce4j-jackson** | JSON event serialization and deserialization using Jackson. |
