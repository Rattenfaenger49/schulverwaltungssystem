package com.school_system.exception;

public class ExistByNameException extends RuntimeException{
    public ExistByNameException(String message) {
        super(message);
    }
}
