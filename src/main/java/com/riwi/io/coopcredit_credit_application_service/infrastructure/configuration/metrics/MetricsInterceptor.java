package com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsInterceptor implements HandlerInterceptor {

    private final Timer httpRequestTimer;
    private final Counter httpErrorsCounter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            
            // Record request duration
            httpRequestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            // Record errors (4xx and 5xx status codes)
            if (response.getStatus() >= 400) {
                httpErrorsCounter.increment();
                log.warn("HTTP error: {} {} - Status: {} - Duration: {}ms", method, uri, response.getStatus(), duration);
            } else {
                log.debug("HTTP request: {} {} - Status: {} - Duration: {}ms", method, uri, response.getStatus(), duration);
            }
        }
    }
}

