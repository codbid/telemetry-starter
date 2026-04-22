package com.codbid.telemetry.manual;

import java.util.Map;
import java.util.function.Supplier;

public interface TelemetryManual {

    void success(String operation);

    void success(String operation, Map<String, String> tags);

    void error(String operation, Throwable throwable);

    void error(String operation, Throwable throwable, Map<String, String> tags);

    <T> T track(String operation, Supplier<T> action);

    <T> T track(String operation, Map<String, String> tags, Supplier<T> action);

    void track(String operation, Runnable action);

    void track(String operation, Map<String, String> tags, Runnable action);
}
