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

package io.github.omidp.eventsauce4j.testcontainers.pgsql;

import java.time.Duration;

public abstract class CommonContainerProperties {

    /**
     * Specify custom Docker image for the container.
     */
    private String dockerImage;

    /**
     * Overrides only version of the Docker image.
     */
    private String dockerImageVersion;
    /**
     * Maximum time in seconds until embedded container should have started.
     */
    private long waitTimeoutInSeconds = 60;
    /**
     * Enable embedded container.
     */
    private boolean enabled = true;

    public Duration getTimeoutDuration() {
        return Duration.ofSeconds(waitTimeoutInSeconds);
    }

    public abstract String getDefaultDockerImage();

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String getDockerImageVersion() {
        return dockerImageVersion;
    }

    public void setDockerImageVersion(String dockerImageVersion) {
        this.dockerImageVersion = dockerImageVersion;
    }

    public long getWaitTimeoutInSeconds() {
        return waitTimeoutInSeconds;
    }

    public void setWaitTimeoutInSeconds(long waitTimeoutInSeconds) {
        this.waitTimeoutInSeconds = waitTimeoutInSeconds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}