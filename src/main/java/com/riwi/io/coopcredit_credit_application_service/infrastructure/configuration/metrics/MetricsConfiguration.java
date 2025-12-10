package com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MetricsConfiguration {

    private final MeterRegistry meterRegistry;

    @Bean
    public Counter httpErrorsCounter() {
        return Counter.builder("http.errors.total")
                .description("Total number of HTTP errors")
                .tag("application", "coopcredit-credit-application-service")
                .register(meterRegistry);
    }

    @Bean
    public Counter authenticationFailuresCounter() {
        return Counter.builder("authentication.failures.total")
                .description("Total number of authentication failures")
                .tag("application", "coopcredit-credit-application-service")
                .register(meterRegistry);
    }

    @Bean
    public Timer httpRequestTimer() {
        return Timer.builder("http.request.duration")
                .description("HTTP request duration")
                .tag("application", "coopcredit-credit-application-service")
                .register(meterRegistry);
    }
}

