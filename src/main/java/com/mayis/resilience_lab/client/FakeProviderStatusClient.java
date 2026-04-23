package com.mayis.resilience_lab.client;

import com.mayis.resilience_lab.exception.PermanentDownstreamException;
import com.mayis.resilience_lab.exception.ProviderNotFoundException;
import com.mayis.resilience_lab.exception.TransientDownstreamException;
import com.mayis.resilience_lab.model.ProviderState;
import com.mayis.resilience_lab.model.ProviderStatus;
import org.springframework.stereotype.Component;


@Component
public class FakeProviderStatusClient implements ProviderStatusClient {

    public ProviderStatus getStatus(String providerId) {
        if (providerId == null || providerId.isBlank()) {
            throw new ProviderNotFoundException("Provider id must not be blank");
        }
        if ("timeout".equalsIgnoreCase(providerId)) {
            throw new TransientDownstreamException("Simulated downstream timeout");
        }

        if ("unavailable".equalsIgnoreCase(providerId)) {
            throw new TransientDownstreamException("Simulated downstream temporary unavailability");
        }

        if ("bad-request".equalsIgnoreCase(providerId)) {
            throw new PermanentDownstreamException("Simulated downstream bad request");
        }

        return new ProviderStatus(providerId, ProviderState.AVAILABLE, "DOWNSTREAM");
    }
}
