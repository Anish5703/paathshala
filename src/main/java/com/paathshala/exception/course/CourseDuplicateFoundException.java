package com.paathshala.exception.course;

public class CourseDuplicateFoundException extends RuntimeException {
    public CourseDuplicateFoundException(String message) {
        super(message);
    }
}
