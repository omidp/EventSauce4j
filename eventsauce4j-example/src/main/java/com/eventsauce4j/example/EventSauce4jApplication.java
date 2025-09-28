package com.eventsauce4j.example;

import com.eventsauce4j.config.EnableEventSauce4j;
import com.eventsauce4j.domain.event.OrderStarted;
import com.eventsauce4j.event.EventDispatcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;

@SpringBootApplication
@EnableTransactionManagement
@EnableEventSauce4j
@EntityScan(basePackages = "com.eventsauce4j.jpa")
public class EventSauce4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSauce4jApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(EventDispatcher eventDispatcher) {
		return args -> {
			eventDispatcher.dispatch(new OrderStarted(UUID.randomUUID(), "order started"));
		};
	}

}
