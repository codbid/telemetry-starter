package com.codbid.telemetry.sender;

import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryEventType;
import com.codbid.telemetry.model.TelemetryStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KafkaTelemetrySender implements TelemetrySender {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTelemetrySender.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String defaultTopic;
    private final Map<TelemetryEventType, String> topics;

    public KafkaTelemetrySender(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            String defaultTopic,
            Map<TelemetryEventType, String> topics
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.defaultTopic = defaultTopic;
        this.topics = topics;
    }

    @Override
    public void send(TelemetryEvent event) {
        final String json;

        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize telemetry event: {}", event, e);
            return;
        }

        TelemetryEventType eventType = resolveEventType(event);
        String topic = topics.getOrDefault(eventType, defaultTopic);
        String key = eventType.name();

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topic, key, json);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error(
                        "Failed to send telemetry event to Kafka. topic={}, key={}, eventId={}",
                        topic,
                        key,
                        event.getEventId(),
                        ex
                );
                return;
            }

            if (result != null) {
                logger.debug(
                        "Telemetry event sent successfully. topic={}, partition={}, offset={}, eventId={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.getEventId()
                );
            }
        });
    }

    private TelemetryEventType resolveEventType(TelemetryEvent event) {
        if (event.getStatus() == TelemetryStatus.ERROR) {
            return TelemetryEventType.ERROR;
        }
        return TelemetryEventType.REQUEST;
    }
}
