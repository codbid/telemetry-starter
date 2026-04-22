package com.codbid.telemetry.aspect;

import com.codbid.telemetry.annotation.Telemetry;
import com.codbid.telemetry.context.TelemetryContextProvider;
import com.codbid.telemetry.customizer.TelemetryEventCustomizerChain;
import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryKind;
import com.codbid.telemetry.model.TelemetryStatus;
import com.codbid.telemetry.sender.TelemetrySender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
public class TelemetryAspect {

    private final TelemetrySender sender;
    private final TelemetryContextProvider contextProvider;
    private final TelemetryEventCustomizerChain customizerChain;

    public TelemetryAspect(
            TelemetrySender sender,
            TelemetryContextProvider contextProvider,
            TelemetryEventCustomizerChain customizerChain
    ) {
        this.sender = sender;
        this.contextProvider = contextProvider;
        this.customizerChain = customizerChain;
    }

    @Around("@annotation(telemetry)")
    public Object around(ProceedingJoinPoint joinPoint, Telemetry telemetry) throws Throwable {
        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();
        String componentName = resolveComponent(joinPoint, telemetry);
        String operation = resolveOperation(methodName, telemetry);

        Throwable error = null;

        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            sendBusinessEvent(start, operation, componentName, methodName, telemetry, error);
        }
    }

    private void sendBusinessEvent(
            long start,
            String operation,
            String componentName,
            String methodName,
            Telemetry telemetry,
            Throwable error
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

        event.setEnvironment(contextProvider.getEnvironment());
        event.setService(contextProvider.getService());
        event.setInstanceId(contextProvider.getInstanceId());

        event.setOperation(operation);
        event.setComponent(componentName);
        event.setMethod(methodName);
        event.setKind(resolveKind(telemetry));
        event.setStatus(error == null ? TelemetryStatus.SUCCESS : TelemetryStatus.ERROR);

        event.setDurationMs(now - start);
        event.setErrorType(error != null ? error.getClass().getSimpleName() : null);
        event.setErrorCode(null);
        event.setStatusCode(null);

        event.setTraceId(traceId);
        event.setSpanId(UUID.randomUUID().toString());
        event.setCorrelationId(correlationId);

        Map<String, String> tags = new HashMap<>(contextProvider.getTags());
        tags.putAll(parseAnnotationTags(telemetry.tags()));
        event.setTags(tags);

        TelemetryEvent customized = customizerChain.customize(event);
        sender.send(customized);
    }

    private String resolveOperation(String methodName, Telemetry telemetry) {
        if (telemetry.operation() != null && !telemetry.operation().trim().isEmpty()) {
            return telemetry.operation();
        }
        if (telemetry.value() != null && !telemetry.value().trim().isEmpty()) {
            return telemetry.value();
        }
        return methodName;
    }

    private String resolveComponent(ProceedingJoinPoint joinPoint, Telemetry telemetry) {
        if (telemetry.component() != null && !telemetry.component().trim().isEmpty()) {
            return telemetry.component();
        }
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    private TelemetryKind resolveKind(Telemetry telemetry) {
        return telemetry.kind() != null ? telemetry.kind() : TelemetryKind.BUSINESS;
    }

    private Map<String, String> parseAnnotationTags(String[] rawTags) {
        Map<String, String> tags = new HashMap<>();
        if (rawTags == null) {
            return tags;
        }

        for (String rawTag : rawTags) {
            if (rawTag == null || rawTag.trim().isEmpty()) {
                continue;
            }

            int index = rawTag.indexOf('=');
            if (index <= 0 || index == rawTag.length() - 1) {
                continue;
            }

            String key = rawTag.substring(0, index).trim();
            String value = rawTag.substring(index + 1).trim();

            if (!key.isEmpty() && !value.isEmpty()) {
                tags.put(key, value);
            }
        }

        return tags;
    }
}