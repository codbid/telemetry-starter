package com.codbid.telemetry.annotation;

import com.codbid.telemetry.model.TelemetryKind;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Telemetry {

    String value() default "";

    String operation() default "";

    String component() default "";

    TelemetryKind kind() default TelemetryKind.BUSINESS;

    String[] tags() default {};
}
