package com.mayis.resilience_lab.exception;

public class TransientDownstreamException extends RuntimeException {
    public TransientDownstreamException(String message) {
        super(message);
    }
}