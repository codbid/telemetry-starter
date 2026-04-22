package com.codbid.telemetry.web;

import com.codbid.telemetry.context.TelemetryContextProvider;
import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryKind;
import com.codbid.telemetry.model.TelemetryStatus;
import com.codbid.telemetry.sender.TelemetrySender;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TelemetryFilter implements Filter {

    private final TelemetrySender sender;
    private final TelemetryContextProvider contextProvider;

    public TelemetryFilter(TelemetrySender sender, TelemetryContextProvider contextProvider) {
        this.sender = sender;
        this.contextProvider = contextProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long start = System.currentTimeMillis();

        String traceId = resolveTraceId(httpRequest);
        String correlationId = resolveCorrelationId(httpRequest, traceId);

        MDC.put("traceId", traceId);
        MDC.put("correlationId", correlationId);

        httpRequest.setAttribute("telemetry.traceId", traceId);
        httpRequest.setAttribute("telemetry.correlationId", correlationId);

        ResponseWrapper wrappedResponse = new ResponseWrapper(httpResponse);

        Throwable error = null;

        try {
            chain.doFilter(request, wrappedResponse);
        } catch (Throwable ex) {
            error = ex;
            throw ex;
        } finally {
            try {
                sendHttpEvent(httpRequest, wrappedResponse, start, traceId, correlationId, error);
            } finally {
                MDC.clear();
            }
        }
    }

    private void sendHttpEvent(
            HttpServletRequest request,
            ResponseWrapper response,
            long start,
            String traceId,
            String correlationId,
            Throwable error
    ) {
        long now = System.currentTimeMillis();
        int statusCode = response.getStatus();

        TelemetryStatus status = resolveStatus(statusCode, error);

        TelemetryEvent event = new TelemetryEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(Instant.ofEpochMilli(now).toString());

        event.setService(contextProvider.getService());
        event.setEnvironment(contextProvider.getEnvironment());
        event.setInstanceId(contextProvider.getInstanceId());

        event.setOperation(request.getMethod() + " " + request.getRequestURI());
        event.setComponent("HttpFilter");
        event.setMethod(null);
        event.setKind(TelemetryKind.HTTP);
        event.setStatus(status);
        event.setStatusCode(statusCode);

        event.setDurationMs(now - start);
        event.setErrorType(error != null ? error.getClass().getSimpleName() : null);
        event.setErrorCode(null);

        event.setTraceId(traceId);
        event.setSpanId(UUID.randomUUID().toString());
        event.setCorrelationId(correlationId);

        Map<String, String> tags = new HashMap<>();
        tags.put("http.method", request.getMethod());
        tags.put("http.path", request.getRequestURI());
        tags.put("http.status", String.valueOf(statusCode));
        event.setTags(tags);

        sender.send(event);
    }

    private String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-B3-TraceId");
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

    private String resolveCorrelationId(HttpServletRequest request, String traceId) {
        String correlationId = request.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = traceId;
        }
        return correlationId;
    }

    private TelemetryStatus resolveStatus(int statusCode, Throwable error) {
        if (error != null || statusCode >= 400) {
            return TelemetryStatus.ERROR;
        }
        return TelemetryStatus.SUCCESS;
    }
}