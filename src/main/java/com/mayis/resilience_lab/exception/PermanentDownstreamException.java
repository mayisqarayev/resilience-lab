package com.mayis.resilience_lab.exception;

public class PermanentDownstreamException extends RuntimeException {
    public PermanentDownstreamException(String message) {
        super(message);
    }
}