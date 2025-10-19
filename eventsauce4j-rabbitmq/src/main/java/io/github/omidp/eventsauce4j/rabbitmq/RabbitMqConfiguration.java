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

package io.github.omidp.eventsauce4j.rabbitmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "eventsauce4j.rabbitmq")
public class RabbitMqConfiguration {

	@Value("${eventsauce4j.rabbitmq.host:localhost}")
	private String host;
	@Value("${eventsauce4j.rabbitmq.port:5672}")
	private int port;
	@Value("${eventsauce4j.rabbitmq.username:guest}")
	private String username;
	@Value("${eventsauce4j.rabbitmq.password:guest}")
	private String password;
	@Value("${eventsauce4j.rabbitmq.vhost:/}")
	private String vhost;
	@Value("${eventsauce4j.rabbitmq.exchange:eventsauce4j.exchange}")
	private String exchange;
	@Value("${eventsauce4j.rabbitmq.queue:eventsauce4j.queue}")
	private String queue;
	@Value("${eventsauce4j.rabbitmq.routingKeys:eventsauce4j.key}")
	private List<String> routingKeys;
	@Value("${eventsauce4j.rabbitmq.dlx:eventsauce4j.dlx}")
	private String dlx;
	@Value("${eventsauce4j.rabbitmq.dlq:eventsauce4j.dlq}")
	private String dlq;
	@Value("${eventsauce4j.rabbitmq.dlk:eventsauce4j.dlk}")
	private String dlk;
	@Value("${eventsauce4j.rabbitmq.messageTtl:300000}")
	private int messageTtl;

	public int getMessageTtl() {
		return messageTtl;
	}

	public void setMessageTtl(int messageTtl) {
		this.messageTtl = messageTtl;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public List<String> getRoutingKeys() {
		return routingKeys;
	}

	public void setRoutingKeys(List<String> routingKeys) {
		this.routingKeys = routingKeys;
	}

	public String getDlx() {
		return dlx;
	}

	public void setDlx(String dlx) {
		this.dlx = dlx;
	}

	public String getDlq() {
		return dlq;
	}

	public void setDlq(String dlq) {
		this.dlq = dlq;
	}

	public String getDlk() {
		return dlk;
	}

	public void setDlk(String dlk) {
		this.dlk = dlk;
	}
}