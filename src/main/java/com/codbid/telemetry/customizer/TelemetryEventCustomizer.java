package com.codbid.telemetry.customizer;

import com.codbid.telemetry.model.TelemetryEvent;

public interface TelemetryEventCustomizer {
    TelemetryEvent customize(TelemetryEvent event);
}