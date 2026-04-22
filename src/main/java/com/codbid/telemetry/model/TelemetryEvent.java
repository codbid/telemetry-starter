package com.codbid.telemetry.model;

import java.util.Map;

public class TelemetryEvent {

    private String eventId;
    private String timestamp;

    private String service;
    private String environment;
    private String instanceId;

    private String operation;
    private String component;
    private String method;
    private TelemetryKind kind;
    private TelemetryStatus status;
    private Integer statusCode;

    private Long durationMs;

    private String errorType;
    private String errorCode;

    private String traceId;
    private String spanId;
    private String correlationId;

    private Map<String, String> tags;

    public TelemetryEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public TelemetryKind getKind() {
        return kind;
    }

    public void setKind(TelemetryKind kind) {
        this.kind = kind;
    }

    public TelemetryStatus getStatus() {
        return status;
    }

    public void setStatus(TelemetryStatus status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "TelemetryEvent{" +
                "eventId='" + eventId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", service='" + service + '\'' +
                ", environment='" + environment + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", operation='" + operation + '\'' +
                ", component='" + component + '\'' +
                ", method='" + method + '\'' +
                ", kind=" + kind +
                ", status=" + status +
                ", statusCode=" + statusCode +
                ", durationMs=" + durationMs +
                ", errorType='" + errorType + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", tags=" + tags +
                '}';
    }
}
