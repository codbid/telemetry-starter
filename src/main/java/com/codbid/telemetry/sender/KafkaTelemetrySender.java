package com.codbid.telemetry.sender;

import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

public class KafkaTelemetrySender implements TelemetrySender{

    private final KafkaTemplate<TelemetryEventType, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String defaultTopic;
    private final Map<TelemetryEventType, String> topics;

    public KafkaTelemetrySender(KafkaTemplate<TelemetryEventType, String> kafkaTemplate,
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

            String topic = topics.getOrDefault(event.getEventType(), defaultTopic);

            System.out.println("TOPIC = " + topic);
            System.out.println("EVENT = " + json);

            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize event: " + event);
        }
    }
}
