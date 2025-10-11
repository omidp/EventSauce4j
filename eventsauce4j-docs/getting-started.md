### Modules in this guide

`eventsauce4j-jpa-starter` : JPA outbox + synchronous dispatch

`eventsauce4j-rabbitmq-starter` : RabbitMQ producer/consumer integration

___

### Prerequisites

- Java 17+
- Spring Boot (recommended)
- Maven
- Postgres
- (For RabbitMQ starter) a running RabbitMQ broker

#### 1) JPA Starter

##### 1.1 Add the dependency

```xml
<dependency>
  <groupId>io.github.omidp</groupId>
  <artifactId>eventsauce4j-jpa-starter</artifactId>
  <version>${latest_stable_version}</version>
</dependency>
```

##### 1.2 Enable the starter

```java
import io.github.omidp.starter.jpa.EnableJpaEventSauce4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJpaEventSauce4j
public class Config {

  @Bean
  Inflector inflection() {
    return new ChainInflector(List.of(
      new ExternalInflector("io.github.omidp.example.domain.event.external"),
      new AnnotationInflector(UserCreated.class.getPackageName()),
      new StaticInflector(Map.of(
        EmailSent.class.getName(), EmailSent.class
      ))
    ));
  }
}
```

___

#### 2) RabbitMQ Starter

##### 2.1 Add the dependency

```xml
<dependency>
	<groupId>io.github.omidp</groupId>
	<artifactId>eventsauce4j-rabbitmq-starter</artifactId>
	<version>${latest_stable_version}</version>
</dependency>
```

##### 2.2 Enable the starter

```java
import io.github.omidp.starter.rabbitmq.EnableRabbitMqEventSauce4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbitMqEventSauce4j
public class Config {

  @Bean
  Inflector inflection() {
    return new ChainInflector(List.of(
      new ExternalInflector("io.github.omidp.example.domain.event.external"),
      new AnnotationInflector(UserCreated.class.getPackageName()),
      new StaticInflector(Map.of(
        EmailSent.class.getName(), EmailSent.class
      ))
    ));
  }
}
```

#### 3) Start RabbitMQ (for RabbitMQ starter)

Run a local broker with Docker:

```
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3.13-management
```

Management UI: http://localhost:15672 (user/pass guest).

#### 4) Configuration

RabbitMQ settings (application.yml)

```yaml
eventsauce4j:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    exchange: eventsauce4j.exchange
    routingKey: eventsauce4j.key
```

#### Outbox settings

Use Spring properties to tune outbox publishing:

`eventsauce4j.outbox.delayInterval` : delay between outbox publish cycles

`eventsauce4j.outbox.lockName` : lock name to isolate publishers per service

Example:

```yaml
eventsauce4j:
  outbox:
    delayInterval: 5 #seconds
    lockName: user-service-outbox-lock
```

#### Working Example

See eventsauce4j-example: two Spring Boot services (user-service and payment-service) demonstrating JPA outbox + RabbitMQ messaging and shared event mappings.

#### Database Schema Setup (PostgreSQL)

When using the **JPA Outbox Starter**, eventsauce4j relies on a set of tables to persist and publish events.
You’ll find a ready-to-use SQL script named schema.sql in the project.

**Alternatively**, you can configure Hibernate to automatically generate the tables for you.

This script defines the required tables for **PostgreSQL**, including:

**Event Store table / Outbox table** : to persist domain events and manage pending events for dispatch

**Lock table** : to prevent duplicate event consumption in distributed environments