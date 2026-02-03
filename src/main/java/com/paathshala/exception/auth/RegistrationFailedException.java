package com.paathshala.exception.auth;

public class RegistrationFailedException extends RuntimeException{
    public RegistrationFailedException(String message)
    {
        super(message);
    }
}
