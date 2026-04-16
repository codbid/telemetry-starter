package com.codbid.telemetry.sender;

import com.codbid.telemetry.model.TelemetryEvent;

public class LogTelemetrySender implements TelemetrySender{

    @Override
    public void send(TelemetryEvent event) {
        System.out.println(event);
    }
}
