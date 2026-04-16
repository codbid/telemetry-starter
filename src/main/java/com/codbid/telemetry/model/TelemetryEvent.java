package com.codbid.telemetry.model;

public class TelemetryEvent {

    private TelemetryEventType eventType;
    private String service;
    private String instance;
    private String operation;

    private long timestamp;
    private long durationMs;

    private boolean success;
    private String errorType;

    private Long requestSizeBytes;

    public TelemetryEvent() {
    }

    public TelemetryEvent(
            TelemetryEventType eventType,
            String service,
            String instance,
            String operation,
            long timestamp,
            long durationMs,
            boolean success,
            String errorType,
            Long requestSizeBytes
    ) {
        this.eventType = eventType;
        this.service = service;
        this.instance = instance;
        this.operation = operation;
        this.timestamp = timestamp;
        this.durationMs = durationMs;
        this.success = success;
        this.errorType = errorType;
        this.requestSizeBytes = requestSizeBytes;
    }

    public TelemetryEventType getEventType() {
        return eventType;
    }

    public void setEventType(TelemetryEventType eventType) {
        this.eventType = eventType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public Long getRequestSizeBytes() {
        return requestSizeBytes;
    }

    public void setRequestSizeBytes(Long requestSizeBytes) {
        this.requestSizeBytes = requestSizeBytes;
    }

    @Override
    public String toString() {
        return "TelemetryEvent{" +
                "eventType='" + eventType.name() + '\'' +
                ", service='" + service + '\'' +
                ", instance='" + instance + '\'' +
                ", operation='" + operation + '\'' +
                ", timestamp=" + timestamp +
                ", durationMs=" + durationMs +
                ", success=" + success +
                ", errorType='" + errorType + '\'' +
                ", requestSizeBytes=" + requestSizeBytes +
                '}';
    }
}
