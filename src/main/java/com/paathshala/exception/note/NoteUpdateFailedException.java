package com.paathshala.exception.note;

public class NoteUpdateFailedException extends RuntimeException{
    public NoteUpdateFailedException(String message) {
        super(message);
    }
}
