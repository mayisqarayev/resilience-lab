package com.mayis.resilience_lab.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProviderStatus {

    String providerId;
    ProviderState status;
    String source;

    public ProviderStatus(String providerId, ProviderState status, String source) {
        this.providerId = providerId;
        this.status = status;
        this.source = source;
    }
}
