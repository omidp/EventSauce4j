### Add a dependency

```
<dependency>
			<groupId>io.eventsauce4j</groupId>
			<artifactId>eventsauce4j-rabbitmq-starter</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
```

### Enable configuration

```
@Configuration
//@EnableJpaEventSauce4j
@EnableRabbitMqEventSauce4j
public class Config {

	@Bean
	Inflector inflection() {
		return new ChainInflector(List.of(
			new ExternalInflector("io.eventsauce4j.example.domain.event.external"),
			new AnnotationInflector(UserCreated.class.getPackageName()),
			new StaticInflector(Map.of(
				EmailSent.class.getName(), EmailSent.class
			))
		));
	}
}
```