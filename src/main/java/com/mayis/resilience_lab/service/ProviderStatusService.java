package com.mayis.resilience_lab.service;

import com.mayis.resilience_lab.client.ProviderStatusClient;
import com.mayis.resilience_lab.dto.ProviderStatusResponse;
import com.mayis.resilience_lab.exception.TransientDownstreamException;
import com.mayis.resilience_lab.model.ProviderState;
import com.mayis.resilience_lab.model.ProviderStatus;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class ProviderStatusService {

    private final ProviderStatusClient client;

    public ProviderStatusService(ProviderStatusClient client) {
        this.client = client;
    }

    @Retry(name = "providerStatusRetry", fallbackMethod = "getProviderStatusFallback")
    public ProviderStatusResponse getProviderStatus(String providerId) {
        ProviderStatus providerStatus = client.getStatus(providerId);

        return new ProviderStatusResponse(
                providerStatus.getProviderId(),
                providerStatus.getStatus(),
                providerStatus.getSource()
        );
    }

    public ProviderStatusResponse getProviderStatusFallback(String providerId, TransientDownstreamException ex) {
        return new ProviderStatusResponse(
                providerId,
                ProviderState.DEGRADED,
                "FALLBACK"
        );
    }
}