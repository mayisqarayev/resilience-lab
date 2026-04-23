package com.mayis.resilience_lab.service;

import com.mayis.resilience_lab.client.ProviderStatusClient;
import com.mayis.resilience_lab.dto.ProviderStatusResponse;
import com.mayis.resilience_lab.exception.TransientDownstreamException;
import com.mayis.resilience_lab.model.ProviderState;
import com.mayis.resilience_lab.model.ProviderStatus;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class ProviderStatusService {

    private final ProviderStatusClient client;
    private final ProviderMetricsService providerMetricsService;
    private final MeterRegistry meterRegistry;

    public ProviderStatusService(ProviderStatusClient client, ProviderMetricsService providerMetricsService, MeterRegistry meterRegistry) {
        this.client = client;
        this.providerMetricsService = providerMetricsService;
        this.meterRegistry = meterRegistry;
    }

    @Retry(name = "providerStatusRetry", fallbackMethod = "getProviderStatusFallback")
    public ProviderStatusResponse getProviderStatus(String providerId) {
        return Timer.builder("provider.status.request.latency")
                .description("Latency of provider status requests")
                .register(meterRegistry)
                .record(() -> {
                    ProviderStatus providerStatus = client.getStatus(providerId);

                    providerMetricsService.incrementSuccess();

                    return new ProviderStatusResponse(
                            providerStatus.getProviderId(),
                            providerStatus.getStatus(),
                            providerStatus.getSource()
                    );
                });
    }

    public ProviderStatusResponse getProviderStatusFallback(String providerId, TransientDownstreamException ex) {
        providerMetricsService.incrementFallback();

        return new ProviderStatusResponse(
                providerId,
                ProviderState.DEGRADED,
                "FALLBACK"
        );
    }
}