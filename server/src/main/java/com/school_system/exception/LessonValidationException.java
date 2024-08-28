package com.school_system.exception;

public class LessonValidationException extends  RuntimeException{
    public LessonValidationException(String message) {
        super(message);
    }

    public LessonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
