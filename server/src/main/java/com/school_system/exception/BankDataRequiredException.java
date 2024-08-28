package com.school_system.exception;

public class BankDataRequiredException extends RuntimeException {
    public BankDataRequiredException(String message) {
        super(message);
    }

}
