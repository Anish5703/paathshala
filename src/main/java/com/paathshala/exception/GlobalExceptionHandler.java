package com.paathshala.exception;

import com.paathshala.DTO.Category.CategoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles database-related exceptions
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<CategoryResponse> handleDatabaseError(DataAccessException ex) {

        log.error("Database error encountered: {}", ex.getMessage(), ex);

        CategoryResponse resp = new CategoryResponse();
        resp.setError(true);
        resp.setMessage(Map.of(
                "status", "Database error",
                "detail", "The system is currently unable to process your request. Please try again later."
        ));

        return new ResponseEntity<>(resp, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles file & IO related exceptions
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<CategoryResponse> handleIOException(IOException ex) {

        log.error("IO error encountered: {}", ex.getMessage(), ex);

        CategoryResponse resp = new CategoryResponse();
        resp.setError(true);
        resp.setMessage(Map.of(
                "status", "File error",
                "detail", "File processing failed. Please try again later."
        ));

        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
