package com.codbid.telemetry.config;

import com.codbid.telemetry.aspect.TelemetryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelemetryAutoConfiguration {

    @Bean
    public TelemetryAspect telemetryAspect() {
        return new TelemetryAspect();
    }
}
