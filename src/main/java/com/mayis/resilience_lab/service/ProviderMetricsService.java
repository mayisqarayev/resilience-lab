package com.mayis.resilience_lab.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ProviderMetricsService {

    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter fallbackCounter;

    public ProviderMetricsService(MeterRegistry meterRegistry) {
        this.successCounter = Counter.builder("provider.status.success.count")
                .description("Number of successful provider status responses")
                .register(meterRegistry);

        this.failureCounter = Counter.builder("provider.status.failure.count")
                .description("Number of failed provider status responses")
                .register(meterRegistry);

        this.fallbackCounter = Counter.builder("provider.status.fallback.count")
                .description("Number of fallback provider status responses")
                .register(meterRegistry);
    }

    public void incrementSuccess() {
        successCounter.increment();
    }

    public void incrementFailure() {
        failureCounter.increment();
    }

    public void incrementFallback() {
        fallbackCounter.increment();
    }
}