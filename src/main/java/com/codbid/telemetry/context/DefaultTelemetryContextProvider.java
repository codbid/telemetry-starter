package com.codbid.telemetry.context;

import org.slf4j.MDC;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;

public class DefaultTelemetryContextProvider implements TelemetryContextProvider {

    private final String service;
    private final String environment;
    private final String instanceId;

    public DefaultTelemetryContextProvider(String service, String environment) {
        this.service = service;
        this.environment = environment;
        this.instanceId = resolveInstanceId();
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getEnvironment() {
        return environment;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String getTraceId() {
        return MDC.get("traceId");
    }

    @Override
    public String getCorrelationId() {
        return MDC.get("correlationId");
    }

    @Override
    public Map<String, String> getTags() {
        return Collections.emptyMap();
    }

    private String resolveInstanceId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-instance";
        }
    }
}