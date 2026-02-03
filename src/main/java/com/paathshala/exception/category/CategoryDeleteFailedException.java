package com.paathshala.exception.category;

public class CategoryDeleteFailedException extends RuntimeException{

    public CategoryDeleteFailedException(String message) {
        super(message);
    }
}
