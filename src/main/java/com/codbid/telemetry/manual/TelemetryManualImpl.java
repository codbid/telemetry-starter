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
import java.util.function.Supplier;

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
        sendManualEvent(operation, TelemetryStatus.SUCCESS, null, tags, null);
    }

    @Override
    public void error(String operation, Throwable throwable) {
        error(operation, throwable, new HashMap<>());
    }

    @Override
    public void error(String operation, Throwable throwable, Map<String, String> tags) {
        sendManualEvent(operation, TelemetryStatus.ERROR, throwable, tags, null);
    }

    @Override
    public <T> T track(String operation, Supplier<T> action) {
        return track(operation, new HashMap<>(), action);
    }

    @Override
    public <T> T track(String operation, Map<String, String> tags, Supplier<T> action) {
        long startNanos = System.nanoTime();
        Throwable error = null;

        try {
            return action.get();
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            Long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            sendManualEvent(
                    operation,
                    error == null ? TelemetryStatus.SUCCESS : TelemetryStatus.ERROR,
                    error,
                    tags,
                    durationMs
            );
        }
    }

    @Override
    public void track(String operation, Runnable action) {
        track(operation, new HashMap<>(), action);
    }

    @Override
    public void track(String operation, Map<String, String> tags, Runnable action) {
        long startNanos = System.nanoTime();
        Throwable error = null;

        try {
            action.run();
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            Long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            sendManualEvent(
                    operation,
                    error == null ? TelemetryStatus.SUCCESS : TelemetryStatus.ERROR,
                    error,
                    tags,
                    durationMs
            );
        }
    }

    private void sendManualEvent(
            String operation,
            TelemetryStatus status,
            Throwable throwable,
            Map<String, String> tags,
            Long durationMs
    ) {
        long now = System.currentTimeMillis();

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
        event.setTimestamp(Instant.ofEpochMilli(now).toString());

        event.setService(contextProvider.getService());
        event.setEnvironment(contextProvider.getEnvironment());
        event.setInstanceId(contextProvider.getInstanceId());

        event.setOperation(operation);
        event.setComponent("TelemetryManual");
        event.setMethod(null);
        event.setKind(TelemetryKind.MANUAL);
        event.setStatus(status);
        event.setStatusCode(null);

        event.setDurationMs(durationMs);
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
