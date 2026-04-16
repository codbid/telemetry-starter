package com.codbid.telemetry.config;

import com.codbid.telemetry.aspect.TelemetryAspect;
import com.codbid.telemetry.sender.LogTelemetrySender;
import com.codbid.telemetry.sender.TelemetrySender;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelemetryAutoConfiguration {

    @Bean
    public TelemetrySender telemetrySender() {
        return new LogTelemetrySender();
    }
    @Bean
    public TelemetryAspect telemetryAspect(
            TelemetrySender sender,
            HttpServletRequest request
    ) {
        return new TelemetryAspect(sender, request);
    }
}
