# EventSauce4j

A lightweight, **event sourcing** toolkit for Java + Spring, inspired by (and conceptually aligned with) [EventSauce](https://eventsauce.io/).  
It provides clean building blocks for **domain events**, **message repositories**, **dispatching**, **outbox**, with a persistence module.

> If you know EventSauce in PHP, you should feel at home: the vocabulary, responsibilities, and flow are intentionally adapted to Java idioms.

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [License](#license)
- [Roadmap](#roadmap)
- [Build](#build)
- [Usage](#usage)

---

## Features

- **Familiar EventSauce model** (Domain Events, Message Repositories/Decorators, Dispatchers).
- **Outbox** support for reliable, exactly-once external publishing.
- **Pluggable serialization** (Jackson by default).

---

## Requirements

- **Java**: 17 or later
- **Build**: Maven 3.8+
- **Database**: PostgreSQL 13+ (recommended 14+)

---

## License
Licensed under the Apache 2.0 License.

---

## Roadmap

- Implement JDBC Outbox pattern.
- Enhance spring boot configuration.
- Add idempotency key handling for consumer de-duplication.

---

## Build

```
mvn clean install
```

## Usage

For reference, see the Spring Boot example project: eventsauce4j-example.