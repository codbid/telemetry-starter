package com.codbid.telemetry.aspect;

import com.codbid.telemetry.annotation.Telemetry;
import com.codbid.telemetry.model.TelemetryEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TelemetryAspect {

    @Around("@annotation(telemetry)")
    public Object around(ProceedingJoinPoint joinPoint, Telemetry telemetry) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            TelemetryEvent event = new TelemetryEvent(
              "demo-service",
                    telemetry.value().isEmpty() ? joinPoint.getSignature().getName() : telemetry.value(),
                    System.currentTimeMillis(),
              System.currentTimeMillis() - start,
              true,
              null
            );

            System.out.println(event);

            return result;
        } catch (Exception e) {
            TelemetryEvent event = new TelemetryEvent(
                    "demo-service",
                    telemetry.value().isEmpty() ? joinPoint.getSignature().getName() : telemetry.value(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis() - start,
                    false,
                    e.getClass().getSimpleName()
            );

            System.out.println(event);

            throw e;
        }
    }
}
