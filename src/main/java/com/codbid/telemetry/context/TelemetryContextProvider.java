package com.codbid.telemetry.context;

import java.util.Map;

public interface TelemetryContextProvider {

    String getService();

    String getEnvironment();

    String getInstanceId();

    String getTraceId();

    String getCorrelationId();

    Map<String, String> getTags();
}