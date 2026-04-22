package com.codbid.telemetry.sender;

import com.codbid.telemetry.model.TelemetryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTelemetrySender implements TelemetrySender{

    private final Logger logger = LoggerFactory.getLogger(LogTelemetrySender.class);

    @Override
    public void send(TelemetryEvent event) {
        logger.info(event.toString());
    }
}
