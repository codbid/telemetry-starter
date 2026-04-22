package com.codbid.telemetry.manual;

import java.util.Map;

public interface TelemetryManual {

    void success(String operation);

    void success(String operation, Map<String, String> tags);

    void error(String operation, Throwable throwable);

    void error(String operation, Throwable throwable, Map<String, String> tags);
}