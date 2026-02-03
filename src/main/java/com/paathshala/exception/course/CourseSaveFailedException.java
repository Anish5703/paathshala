package com.paathshala.exception.course;

public class CourseSaveFailedException extends RuntimeException {
    public CourseSaveFailedException(String message) {
        super(message);
    }
}
