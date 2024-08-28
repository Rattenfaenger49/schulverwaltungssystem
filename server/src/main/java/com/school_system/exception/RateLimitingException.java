package com.school_system.exception;

public class RateLimitingException extends RuntimeException {
    // Default constructor
    public RateLimitingException() {
        super("Gesperrt aufgrund zu vieler fehlgeschlagener Versuche.");
    }

    // Constructor that accepts a custom message
    public RateLimitingException(String message) {
        super(message);
    }
}
