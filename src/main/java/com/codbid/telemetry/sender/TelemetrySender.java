package com.codbid.telemetry.sender;

import com.codbid.telemetry.model.TelemetryEvent;

public interface TelemetrySender {
    void send(TelemetryEvent event);
}
