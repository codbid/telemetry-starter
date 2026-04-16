package com.codbid.telemetry.config;

import com.codbid.telemetry.aspect.TelemetryAspect;
import com.codbid.telemetry.model.TelemetryEventType;
import com.codbid.telemetry.sender.KafkaTelemetrySender;
import com.codbid.telemetry.sender.TelemetrySender;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({
        TelemetryKafkaProperties.class,
        TelemetryKafkaProducerProperties.class
})
public class TelemetryAutoConfiguration {

    @Bean ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TelemetrySender telemetrySender(
            KafkaTemplate<TelemetryEventType, String> kafkaTemplate,
            ObjectMapper objectMapper,
            TelemetryKafkaProperties properties
    ) {
        return new KafkaTelemetrySender(
                kafkaTemplate,
                objectMapper,
                properties.getDefaultTopic(),
                properties.getTopics()
        );
    }

    @Bean
    public TelemetryAspect telemetryAspect(
            TelemetrySender sender,
            HttpServletRequest request,
            @Value("${spring.application.name:unknown-service}") String serviceName
    ) {
        return new TelemetryAspect(
                sender,
                request,
                serviceName
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<TelemetryEventType, String> telemetryKafkaTemplate(
            TelemetryKafkaProducerProperties t,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ) {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // reliability
        props.put(ProducerConfig.ACKS_CONFIG, t.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, t.getRetries());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, t.getEnableIdempotence());

        // batching
        props.put(ProducerConfig.LINGER_MS_CONFIG, t.getLingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, t.getBatchSize());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, t.getBufferMemory());

        // compression
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, t.getCompressionType());

        // timeouts
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, t.getDeliveryTimeoutMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, t.getRequestTimeoutMs());

        // networking
        props.put(ProducerConfig.SEND_BUFFER_CONFIG, t.getSendBufferBytes());
        props.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, t.getReceiveBufferBytes());
        props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, t.getConnectionsMaxIdleMs());

        // retries / backoff
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, t.getRetryBackoffMs());
        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, t.getReconnectBackoffMs());
        props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, t.getReconnectBackoffMaxMs());

        // metadata
        props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, t.getMetadataMaxAgeMs());

        // inflight
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, t.getMaxInFlightRequests());

        // limits
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, t.getMaxRequestSize());

        // client
        props.put(ProducerConfig.CLIENT_ID_CONFIG, t.getClientId());

        ProducerFactory<TelemetryEventType, String> factory =
                new DefaultKafkaProducerFactory<>(props);

        return new KafkaTemplate<>(factory);
    }
}
