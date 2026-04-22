package com.codbid.telemetry.manual;

import com.codbid.telemetry.context.TelemetryContextProvider;
import com.codbid.telemetry.customizer.TelemetryEventCustomizerChain;
import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryKind;
import com.codbid.telemetry.model.TelemetryStatus;
import com.codbid.telemetry.sender.TelemetrySender;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TelemetryManualImpl implements TelemetryManual {

    private final TelemetrySender sender;
    private final TelemetryContextProvider contextProvider;
    private final TelemetryEventCustomizerChain customizerChain;

    public TelemetryManualImpl(
            TelemetrySender sender,
            TelemetryContextProvider contextProvider,
            TelemetryEventCustomizerChain customizerChain
    ) {
        this.sender = sender;
        this.contextProvider = contextProvider;
        this.customizerChain = customizerChain;
    }

    @Override
    public void success(String operation) {
        success(operation, new HashMap<>());
    }

    @Override
    public void success(String operation, Map<String, String> tags) {
        sendManualEvent(operation, TelemetryStatus.SUCCESS, null, tags);
    }

    @Override
    public void error(String operation, Throwable throwable) {
        error(operation, throwable, new HashMap<>());
    }

    @Override
    public void error(String operation, Throwable throwable, Map<String, String> tags) {
        sendManualEvent(operation, TelemetryStatus.ERROR, throwable, tags);
    }

    private void sendManualEvent(
            String operation,
            TelemetryStatus status,
            Throwable throwable,
            Map<String, String> tags
    ) {
        String traceId = contextProvider.getTraceId();
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        String correlationId = contextProvider.getCorrelationId();
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = traceId;
        }

        TelemetryEvent event = new TelemetryEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(Instant.now().toString());

        event.setService(contextProvider.getService());
        event.setEnvironment(contextProvider.getEnvironment());
        event.setInstanceId(contextProvider.getInstanceId());

        event.setOperation(operation);
        event.setComponent("TelemetryManual");
        event.setMethod(null);
        event.setKind(TelemetryKind.MANUAL);
        event.setStatus(status);
        event.setStatusCode(null);

        event.setDurationMs(null);
        event.setErrorType(throwable != null ? throwable.getClass().getSimpleName() : null);
        event.setErrorCode(null);

        event.setTraceId(traceId);
        event.setSpanId(UUID.randomUUID().toString());
        event.setCorrelationId(correlationId);

        Map<String, String> mergedTags = new HashMap<>(contextProvider.getTags());
        if (tags != null) {
            mergedTags.putAll(tags);
        }
        event.setTags(mergedTags);

        TelemetryEvent customized = customizerChain.customize(event);
        sender.send(customized);
    }
}