package com.codbid.telemetry.aspect;

import com.codbid.telemetry.annotation.Telemetry;
import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryEventType;
import com.codbid.telemetry.sender.TelemetrySender;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TelemetryAspect {

    private final TelemetrySender sender;
    private final HttpServletRequest request;

    private static final String SERVICE_NAME = "demo-service";

    public TelemetryAspect(TelemetrySender sender, HttpServletRequest request) {
        this.sender = sender;
        this.request = request;
    }

    @Around("@annotation(telemetry)")
    public Object around(ProceedingJoinPoint joinPoint, Telemetry telemetry) throws Throwable {

        long start = System.currentTimeMillis();

        String operation = telemetry.value().isEmpty()
                ? joinPoint.getSignature().getName()
                : telemetry.value();

        Long requestSize = extractRequestSize();

        try {
            Object result = joinPoint.proceed();

            TelemetryEvent event = buildEvent(
                    TelemetryEventType.REQUEST,
                    operation,
                    System.currentTimeMillis(),
                    System.currentTimeMillis() - start,
                    true,
                    null,
                    requestSize
            );

            sender.send(event);

            return result;

        } catch (Exception ex) {

            TelemetryEvent event = buildEvent(
                    TelemetryEventType.ERROR,
                    operation,
                    System.currentTimeMillis(),
                    System.currentTimeMillis() - start,
                    false,
                    ex.getClass().getSimpleName(),
                    requestSize
            );

            sender.send(event);

            throw ex;
        }
    }

    private Long extractRequestSize() {
        try {
            long size = request.getContentLengthLong();
            return size >= 0 ? size : null;
        } catch (Exception e) {
            return null;
        }
    }

    private TelemetryEvent buildEvent(
            TelemetryEventType eventType,
            String operation,
            long timestamp,
            long duration,
            boolean success,
            String errorType,
            Long requestSize
    ) {
        return new TelemetryEvent(
                eventType,
                SERVICE_NAME,
                getInstance(),
                operation,
                timestamp,
                duration,
                success,
                errorType,
                requestSize
        );
    }

    private String getInstance() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-instance";
        }
    }
}
