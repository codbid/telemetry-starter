package com.codbid.telemetry.sender;

import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryEventType;
import com.codbid.telemetry.model.TelemetryStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

public class KafkaTelemetrySender implements TelemetrySender{

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String defaultTopic;
    private final Map<TelemetryEventType, String> topics;

    public KafkaTelemetrySender(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                String defaultTopic,
                                Map<TelemetryEventType, String> topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.defaultTopic = defaultTopic;
        this.topics = topics;
    }

    @Override
    public void send(TelemetryEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            TelemetryEventType eventType = resolveEventType(event);
            String topic = topics.getOrDefault(eventType, defaultTopic);

            System.out.println("TOPIC = " + topic);
            System.out.println("EVENT = " + json);

            kafkaTemplate.send(topic, eventType.name(), json);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize event: " + event);
        }
    }

    private TelemetryEventType resolveEventType(TelemetryEvent event) {
        if (event.getStatus() == TelemetryStatus.ERROR) {
            return TelemetryEventType.ERROR;
        }
        return TelemetryEventType.REQUEST;
    }
}
