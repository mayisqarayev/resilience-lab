package com.mayis.resilience_lab.dto;

import com.mayis.resilience_lab.model.ProviderState;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProviderStatusResponse {

    String providerId;
    ProviderState status;
    String source;

    public ProviderStatusResponse() {
    }

    public ProviderStatusResponse(String providerId, ProviderState status, String source) {
        this.providerId = providerId;
        this.status = status;
        this.source = source;
    }
}