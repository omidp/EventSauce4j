/*
 * Copyright 2024–2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.omidp.eventsauce4j.tc;

public class PostgreSQLProperties extends CommonContainerProperties {
    static final String BEAN_NAME_EMBEDDED_POSTGRESQL = "embeddedPostgreSql";

    String user = "test";
    String password = "test";
    String database = "testdb";
    String startupLogCheckRegex;
    /**
     * The SQL file path to execute after the container starts to initialize the database.
     */
    String initScriptPath;

    // https://hub.docker.com/_/postgres
    @Override
    public String getDefaultDockerImage() {
        return "postgres:16.4";
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getStartupLogCheckRegex() {
        return startupLogCheckRegex;
    }

    public void setStartupLogCheckRegex(String startupLogCheckRegex) {
        this.startupLogCheckRegex = startupLogCheckRegex;
    }

    public String getInitScriptPath() {
        return initScriptPath;
    }

    public void setInitScriptPath(String initScriptPath) {
        this.initScriptPath = initScriptPath;
    }
}