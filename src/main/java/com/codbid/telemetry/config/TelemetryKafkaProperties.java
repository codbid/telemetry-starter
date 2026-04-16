package com.codbid.telemetry.config;

import com.codbid.telemetry.model.TelemetryEventType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "telemetry.kafka")
public class TelemetryKafkaProperties {

    private String defaultTopic;

    private Map<TelemetryEventType, String> topics = new HashMap<>();

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public Map<TelemetryEventType, String> getTopics() {
        return topics;
    }

    public void setTopics(Map<TelemetryEventType, String> topics) {
        this.topics = topics;
    }
}
