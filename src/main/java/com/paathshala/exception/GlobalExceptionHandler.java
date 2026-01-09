package com.paathshala.exception;

import com.paathshala.dto.category.CategoryResponse;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.dto.ErrorResponse;
import com.paathshala.model.ErrorType;
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

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<NoteResponse> handleFileNotUpload(FileUploadFailedException ex)
    {
        log.error("File upload error encountered: {}",ex.getLocalizedMessage() );
        NoteResponse resp = new NoteResponse();
        resp.setError(true);
        resp.setMessage(Map.of(
                "status", "Upload failed",
                "detail", ex.getMessage()
        ));
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NoteDeletionFailedException.class)
    public ResponseEntity<NoteResponse> handleNoteNotDeleted(NoteDeletionFailedException ex)
    {
        log.error("Note deletion error encountered: {}",ex.getLocalizedMessage());
        NoteResponse resp = new NoteResponse();
        resp.setError(true);
        resp.setMessage(Map.of(
                "status", "Note deletion failed",
                "detail", ex.getMessage()
        ));
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(CourseNotFoundException ex)
    {
        log.error("Course not found : {}",ex.getLocalizedMessage());
        ErrorResponse resp = new ErrorResponse();
        resp.setErrorType(ErrorType.COURSE_NOT_FOUND);
        resp.setMessage(ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);

    }






}
