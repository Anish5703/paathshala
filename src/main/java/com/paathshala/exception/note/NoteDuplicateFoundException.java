package com.paathshala.exception.note;

public class NoteDuplicateFoundException extends RuntimeException {
    public NoteDuplicateFoundException(String message) {
        super(message);
    }
}
