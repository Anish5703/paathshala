package com.paathshala.exception.auth;

public class DuplicateUsernameFoundException extends RuntimeException {
    public DuplicateUsernameFoundException(String message) {
        super(message);
    }
}
