package com.paathshala.exception.content;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException(String message) {
        super(message);
    }
}
