package com.paathshala.exception.content;

public class ContentDeleteFailedException extends RuntimeException {
    public ContentDeleteFailedException(String message) {
        super(message);
    }
    public ContentDeleteFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
