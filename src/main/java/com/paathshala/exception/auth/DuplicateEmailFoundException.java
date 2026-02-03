package com.paathshala.exception.auth;

public class DuplicateEmailFoundException extends RuntimeException {
    public DuplicateEmailFoundException(String message) {
        super(message);
    }
}
