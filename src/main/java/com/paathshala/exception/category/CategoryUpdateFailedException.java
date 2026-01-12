package com.paathshala.exception.category;

public class CategoryUpdateFailedException extends RuntimeException {
    public CategoryUpdateFailedException(String message) {
        super(message);
    }
}
