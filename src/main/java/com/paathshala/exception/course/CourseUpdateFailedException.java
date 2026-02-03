package com.paathshala.exception.course;

public class CourseUpdateFailedException extends RuntimeException {
    public CourseUpdateFailedException(String message) {
        super(message);
    }
}
