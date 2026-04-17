package com.codbid.telemetry.aspect;

import com.codbid.telemetry.annotation.Telemetry;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
public class TelemetryAspect {

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Around("@annotation(telemetry)")
    public Object around(ProceedingJoinPoint joinPoint, Telemetry telemetry) throws Throwable {

        long start = System.currentTimeMillis();

        HttpServletRequest request = getRequest();

        if (request == null) {
            return joinPoint.proceed();
        }

        Boolean enabled = (Boolean) request.getAttribute("telemetry.enabled");

        if (Boolean.TRUE.equals(enabled)) {
            return joinPoint.proceed();
        }
        String operation = telemetry.value().isEmpty()
                ? joinPoint.getSignature().getName()
                : telemetry.value();

        request.setAttribute("telemetry.enabled", true);
        request.setAttribute("telemetry.operation", operation);
        request.setAttribute("telemetry.start", start);

        try {
            Object result = joinPoint.proceed();

            request.setAttribute("telemetry.success", true);
            request.setAttribute("telemetry.error", null);

            return result;

        } catch (Exception ex) {

            request.setAttribute("telemetry.success", false);
            request.setAttribute("telemetry.error", ex.getClass().getSimpleName());

            throw ex;
        }
    }
}
