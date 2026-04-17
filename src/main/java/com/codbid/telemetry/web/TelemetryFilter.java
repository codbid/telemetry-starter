package com.codbid.telemetry.web;

import com.codbid.telemetry.model.TelemetryEvent;
import com.codbid.telemetry.model.TelemetryEventType;
import com.codbid.telemetry.sender.TelemetrySender;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TelemetryFilter implements Filter {

    private final TelemetrySender sender;
    private final String serviceName;
    private final String instance;

    public TelemetryFilter(TelemetrySender sender, String serviceName) {
        this.sender = sender;
        this.serviceName = serviceName;
        this.instance = resolveInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        ResponseWrapper wrappedResponse =
                new ResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(request, wrappedResponse);
        } finally {

            Boolean enabled = (Boolean) request.getAttribute("telemetry.enabled");

            if (Boolean.TRUE.equals(enabled)) {
                sendEvent(request, wrappedResponse);
            }
        }
    }

    private void sendEvent(ServletRequest request, ResponseWrapper wrappedResponse) {
        Long start = (Long) request.getAttribute("telemetry.start");
        String operation = (String) request.getAttribute("telemetry.operation");
        String error = (String) request.getAttribute("telemetry.error");

        Boolean success = (Boolean) request.getAttribute("telemetry.success");

        int status = wrappedResponse.getStatus();

        if (Boolean.FALSE.equals(success) && status < 400) {
            status = 500;
        }

        long now = System.currentTimeMillis();

        TelemetryEvent event = new TelemetryEvent(
                success ? TelemetryEventType.REQUEST : TelemetryEventType.ERROR,
                serviceName,
                instance,
                operation,
                now,
                now - start,
                success,
                status,
                error,
                request.getContentLengthLong(),
                wrappedResponse.getContentSize()
        );

        sender.send(event);
    }

    private String resolveInstance() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-instance";
        }
    }
}