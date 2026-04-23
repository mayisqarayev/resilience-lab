package com.mayis.resilience_lab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorResponse(
            HttpStatus status,
            Exception ex,
            WebRequest webRequest
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("errorDetails", webRequest.getDescription(false));
        response.put("errorStatus", status.value());
        response.put("errorTimeStamp", Instant.now());

        return response;
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleProviderNotFoundException(ProviderNotFoundException ex, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }


    @ExceptionHandler(PermanentDownstreamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handlePermanentDownstreamException(
            PermanentDownstreamException ex,
            WebRequest webRequest
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    @ExceptionHandler(TransientDownstreamException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> handleTransientDownstreamException(
            TransientDownstreamException ex,
            WebRequest webRequest
    ) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex, webRequest);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGenericException(
            Exception ex,
            WebRequest webRequest
    ) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, webRequest);
    }
}