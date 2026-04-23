package com.mayis.resilience_lab.controller;

import com.mayis.resilience_lab.client.ProviderStatusClient;
import com.mayis.resilience_lab.exception.PermanentDownstreamException;
import com.mayis.resilience_lab.exception.TransientDownstreamException;
import com.mayis.resilience_lab.model.ProviderState;
import com.mayis.resilience_lab.model.ProviderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProviderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProviderStatusClient client;

    @Test
    @DisplayName("Should return 200 and provider status when downstream returns success")
    void shouldReturnProviderStatusWhenRequestIsSuccessful() throws Exception {
        String providerId = "payment-gateway";

        when(client.getStatus(providerId))
                .thenReturn(new ProviderStatus(providerId, ProviderState.AVAILABLE, "DOWNSTREAM"));

        mockMvc.perform(get("/api/v1/providers/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerId").value(providerId))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.source").value("DOWNSTREAM"));
    }

    @Test
    @DisplayName("Should return fallback response when transient downstream error occurs")
    void shouldReturnFallbackResponseWhenTransientErrorOccurs() throws Exception {
        String providerId = "timeout";

        when(client.getStatus(providerId))
                .thenThrow(new TransientDownstreamException("Simulated downstream timeout"));

        mockMvc.perform(get("/api/v1/providers/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerId").value(providerId))
                .andExpect(jsonPath("$.status").value("DEGRADED"))
                .andExpect(jsonPath("$.source").value("FALLBACK"));
    }

    @Test
    @DisplayName("Should return 400 when permanent downstream error occurs")
    void shouldReturnBadRequestWhenPermanentErrorOccurs() throws Exception {
        String providerId = "bad-request";

        when(client.getStatus(providerId))
                .thenThrow(new PermanentDownstreamException("Simulated downstream bad request"));

        mockMvc.perform(get("/api/v1/providers/{providerId}", providerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Simulated downstream bad request"))
                .andExpect(jsonPath("$.path").value("/api/v1/providers/" + providerId));
    }
}