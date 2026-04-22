package com.mayis.resilience_lab.client;

import com.mayis.resilience_lab.model.ProviderStatus;

public interface ProviderStatusClient {
    ProviderStatus getStatus(String providerId);
}
