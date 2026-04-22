package com.mayis.resilience_lab.service;

import com.mayis.resilience_lab.client.ProviderStatusClient;
import com.mayis.resilience_lab.dto.ProviderStatusResponse;
import com.mayis.resilience_lab.model.ProviderStatus;
import org.springframework.stereotype.Service;

@Service
public class ProviderStatusService {

    private final ProviderStatusClient client;

    public ProviderStatusService(ProviderStatusClient client) {
        this.client = client;
    }

    public ProviderStatusResponse getProviderStatus(String providerId) {
        ProviderStatus providerStatus = client.getStatus(providerId);

        return new ProviderStatusResponse(
                providerStatus.getProviderId(),
                providerStatus.getStatus(),
                providerStatus.getSource()
        );
    }
}