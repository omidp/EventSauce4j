/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.eventsauce4j.rmq;// RabbitConfig.java
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public final class RabbitConfig {
    public static final String HOST = "localhost";
    public static final int PORT = 5672;           // default
    public static final String USER = "guest";
    public static final String PASS = "guest";
    public static final String VHOST = "/";

    // Topology
    public static final String EXCHANGE = "demo.exchange";
    public static final BuiltinExchangeType EXCHANGE_TYPE = BuiltinExchangeType.DIRECT;
    public static final String QUEUE = "demo.queue";
    public static final String ROUTING_KEY = "demo.key";

    // Optional dead-lettering
    public static final String DLX = "demo.dlx";
    public static final String DLQ = "demo.dlq";
    public static final String DLK = "demo.dead";

    private RabbitConfig() {}

    public static Connection newConnection() throws Exception {
        ConnectionFactory f = new ConnectionFactory();
        f.setHost(HOST);
        f.setPort(PORT);
        f.setVirtualHost(VHOST);
        f.setUsername(USER);
        f.setPassword(PASS);
        f.setAutomaticRecoveryEnabled(true);     // auto-recover connections
        f.setNetworkRecoveryInterval(5000);
        return f.newConnection();
    }

    /** Idempotently declares exchange/queue/bindings. Call once on startup (producer or consumer). */
    public static void declareTopology(Channel ch) throws Exception {
        // DLX/ DLQ (optional but useful)
        ch.exchangeDeclare(DLX, BuiltinExchangeType.FANOUT, true);
        ch.queueDeclare(DLQ, true, false, false, null);
        ch.queueBind(DLQ, DLX, "");

        // Main exchange
        ch.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE, true);

        // Queue arguments: dead-letter + TTL examples (tune as needed)
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX);  // send rejected/expired here
        args.put("x-message-ttl", 300_000);       // 5 minutes (optional)
        args.put("x-max-length", 50_000);         // cap queue length (optional)

        // Durable, non-exclusive, not auto-delete
        ch.queueDeclare(QUEUE, true, false, false, args);
        ch.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);
    }
}
