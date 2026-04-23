package com.mayis.resilience_lab.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryEventLogger {

    private static final Logger log = LoggerFactory.getLogger(RetryEventLogger.class);

    private final RetryRegistry retryRegistry;

    public RetryEventLogger(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void registerRetryEventListeners() {
        Retry retry = retryRegistry.retry("providerStatusRetry");

        retry.getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retry attempt #{} for '{}' due to: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getName(),
                        event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "unknown"
                ))
                .onSuccess(event -> log.info(
                        "Retry '{}' finished successfully after {} attempt(s)",
                        event.getName(),
                        event.getNumberOfRetryAttempts()
                ))
                .onError(event -> log.error(
                        "Retry '{}' exhausted after {} attempt(s). Last error: {}",
                        event.getName(),
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable() != null ? event.getLastThrowable().getMessage() : "unknown"
                ));
    }
}