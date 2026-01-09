package com.paathshala.exception;

public class NoteDeletionFailedException extends RuntimeException {
    public NoteDeletionFailedException(String message) {
        super(message);
    }
    public NoteDeletionFailedException(String message,Throwable cause)
    {
        super(message, cause);
    }
}
