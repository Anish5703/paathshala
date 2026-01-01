package com.paathshala.exception;

import com.paathshala.DTO.Category.CategoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches all database-related exceptions (Implicitly thrown by Spring/Hibernate)
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<CategoryResponse> handleDatabaseError(DataAccessException ex) {
        // Log the actual error for the developer/admin
        log.error("Database error encountered: {}", ex.getMessage());

        // Create a user-friendly response
        CategoryResponse resp = new CategoryResponse();
        resp.setError(true);
        resp.setMessage(Map.of(
                "status", "Database error",
                "detail", "The system is currently unable to process your request. Please try again later."
        ));

        // Return 503 Service Unavailable or 500 Internal Server Error
        return new ResponseEntity<>(resp, HttpStatus.SERVICE_UNAVAILABLE);
    }
}