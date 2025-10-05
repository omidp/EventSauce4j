# Welcome to EventSauce4j

A lightweight, event sourcing toolkit for Java 17+, inspired by (and conceptually aligned with) EventSauce.
It provides clean building blocks for domain events, message repositories, dispatching, outbox, with a persistence module.

> If you know EventSauce in PHP, you should feel at home: the vocabulary, responsibilities, and flow are intentionally adapted to Java idioms.

## Modules

* `eventsauce4j-jpa` - Synchronous implementation of message dispatcher. 
* `eventsauce4j-jpa` - JPA implementation for outbox. 
* `eventsauce4j-jpa` - producer/consumer for communication between services.
* `eventsauce4j-jackson` - JSON event serialization. 
