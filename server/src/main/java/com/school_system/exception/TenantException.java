package com.school_system.exception;

public class TenantException extends RuntimeException{

    public TenantException(String message) {
        super(message);
    }
    public TenantException(String message, Throwable cause) {
        super(message, cause);
    }
}
