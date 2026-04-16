package com.codbid.telemetry.model;

public class TelemetryEvent {

    private String service;
    private String operation;
    private long timestamp;
    private long duration;
    private boolean success;
    private String errorType;

    public TelemetryEvent(String service, String operation, long timestamp, long duration, boolean success, String errorType) {
        this.service = service;
        this.operation = operation;
        this.timestamp = timestamp;
        this.duration = duration;
        this.success = success;
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "TelemetryEvent{" +
                "service'=" + service + '\'' +
                ", operation='" + operation + '\'' +
                ", timestamp=" + timestamp + '\'' +
                ", duration=" + duration + '\'' +
                ", success=" + success + '\'' +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
