package com.paathshala.exception.course;

public class CourseDeleteFailedException extends RuntimeException {
    public CourseDeleteFailedException(String message) {
        super(message);
    }
}
