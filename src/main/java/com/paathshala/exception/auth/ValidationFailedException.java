package com.paathshala.exception.auth;

public class ValidationFailedException extends RuntimeException{
    public ValidationFailedException(String message)
    {
        super(message);
    }
}
