package com.paathshala.exception.note;

public class NoteDeleteFailedException extends RuntimeException {
    public NoteDeleteFailedException(String message) {
        super(message);
    }
    public NoteDeleteFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
