package com.mayis.resilience_lab.controller;

import com.mayis.resilience_lab.dto.ProviderStatusResponse;
import com.mayis.resilience_lab.service.ProviderStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderStatusController {

    private final ProviderStatusService providerStatusService;

    public ProviderStatusController(ProviderStatusService providerStatusService) {
        this.providerStatusService = providerStatusService;
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderStatusResponse> getProviderStatus(@PathVariable String providerId) {
        return ResponseEntity.ok(providerStatusService.getProviderStatus(providerId));
    }
}