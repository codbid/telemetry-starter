package com.codbid.telemetry.customizer;

import com.codbid.telemetry.model.TelemetryEvent;

import java.util.List;

public class TelemetryEventCustomizerChain {

    private final List<TelemetryEventCustomizer> customizers;

    public TelemetryEventCustomizerChain(List<TelemetryEventCustomizer> customizers) {
        this.customizers = customizers;
    }

    public TelemetryEvent customize(TelemetryEvent event) {
        TelemetryEvent current = event;

        if (customizers == null) {
            return current;
        }

        for (TelemetryEventCustomizer customizer : customizers) {
            if (customizer != null) {
                current = customizer.customize(current);
            }
        }

        return current;
    }
}