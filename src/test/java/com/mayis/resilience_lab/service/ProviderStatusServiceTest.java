package com.mayis.resilience_lab.service;

import com.mayis.resilience_lab.client.ProviderStatusClient;
import com.mayis.resilience_lab.dto.ProviderStatusResponse;
import com.mayis.resilience_lab.exception.PermanentDownstreamException;
import com.mayis.resilience_lab.exception.TransientDownstreamException;
import com.mayis.resilience_lab.model.ProviderState;
import com.mayis.resilience_lab.model.ProviderStatus;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProviderStatusServiceTest {

    private ProviderStatusClient providerStatusClient;
    private ProviderMetricsService providerMetricsService;
    private ProviderStatusService providerStatusService;

    @BeforeEach
    void setUp() {
        providerStatusClient= mock(ProviderStatusClient.class);
        providerMetricsService = mock(ProviderMetricsService.class);

        providerStatusService = new ProviderStatusService(
                providerStatusClient,
                providerMetricsService,
                new SimpleMeterRegistry()
        );
    }

    @Test
    void shouldReturnProviderStatusResponseWhenClientReturnsSuccess() {
        String providerId = "payment-gateway";
        ProviderStatus providerStatus = new ProviderStatus(
                  providerId,
                ProviderState.AVAILABLE,
                "DOWNSTREAM"
        );

        when(providerStatusClient.getStatus(providerId)).thenReturn(providerStatus);
        ProviderStatusResponse response = providerStatusService.getProviderStatus(providerId);
        assertNotNull(response);
        assertEquals(providerId, response.getProviderId());
        assertEquals(ProviderState.AVAILABLE, response.getStatus());
        assertEquals("DOWNSTREAM", response.getSource());

        verify(providerStatusClient, times(1)).getStatus(providerId);
        verify(providerMetricsService, times(1)).incrementSuccess();
        verify(providerMetricsService, never()).incrementFallback();
    }

    @Test
    void shouldThrowPermanentDownstreamExceptionWhenClientThrowsPermanentError() {
        String providerId = "bad-request";

        when(providerStatusClient.getStatus(providerId))
                .thenThrow(new PermanentDownstreamException("Simulated downstream bad request"));

        PermanentDownstreamException exception = assertThrows(
                PermanentDownstreamException.class,
                () -> providerStatusService.getProviderStatus(providerId)
        );

        assertEquals("Simulated downstream bad request", exception.getMessage());

        verify(providerStatusClient, times(1)).getStatus(providerId);
        verify(providerMetricsService, never()).incrementSuccess();
        verify(providerMetricsService, never()).incrementFallback();
    }

    @Test
    void shouldReturnFallbackResponseWhenFallbackMethodIsCalled() {
        String providerId = "timeout";
        TransientDownstreamException exception =
                new TransientDownstreamException("Simulated downstream timeout");

        ProviderStatusResponse response =
                providerStatusService.getProviderStatusFallback(providerId, exception);

        assertNotNull(response);
        assertEquals(providerId, response.getProviderId());
        assertEquals(ProviderState.DEGRADED, response.getStatus());
        assertEquals("FALLBACK", response.getSource());

        verify(providerMetricsService, times(1)).incrementFallback();
        verify(providerMetricsService, never()).incrementSuccess();
        verifyNoInteractions(providerStatusClient);
    }
}