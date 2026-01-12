package com.paathshala.exception;

import com.paathshala.dto.category.CategoryResponse;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.dto.ErrorResponse;
import com.paathshala.exception.category.*;
import com.paathshala.exception.course.CourseDeleteFailedException;
import com.paathshala.exception.course.CourseDuplicateFoundException;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.exception.course.CourseSaveFailedException;
import com.paathshala.exception.note.*;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.ILLEGAL_ARGUMENTS,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ErrorResponse> handleFileNotUpload(FileUploadFailedException ex)
    {
        log.error("File upload error encountered: ",ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.FILE_UPLOAD_FAILED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.CATEGORY_NOT_FOUND,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CategoryDuplicateFoundException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateFound(CategoryDuplicateFoundException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.CATEGORY_ALREADY_EXISTS,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CategorySaveFailedException.class)
    public ResponseEntity<ErrorResponse> handleCategorySaveFailed(CategorySaveFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.CATEGORY_NOT_SAVED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CategoryUpdateFailedException.class)
    public ResponseEntity<ErrorResponse> handleCategoryUpdateFailed(CategoryUpdateFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.CATEGORY_NOT_UPDATED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(CategoryDeleteFailedException.class)
    public ResponseEntity<ErrorResponse> handleCategoryDeleteFailed(CategoryDeleteFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.CATEGORY_NOT_DELETED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(CourseNotFoundException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse();
        resp.setErrorType(ErrorType.COURSE_NOT_FOUND);
        resp.setMessage(ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(CourseDeleteFailedException.class)
    public ResponseEntity<ErrorResponse> handleCourseDuplicateFound(CourseDeleteFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.COURSE_NOT_DELETED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(CourseDuplicateFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseDuplicateFound(CourseDuplicateFoundException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.COURSE_ALREADY_EXISTS,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseSaveFailedException.class)
    public ResponseEntity<ErrorResponse> handleCourseSaveFailed(CourseSaveFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.COURSE_NOT_SAVED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoteNotFound(NoteNotFoundException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.NOTE_NOT_FOUND,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoteDuplicateFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoteDuplicateFound(NoteDuplicateFoundException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.NOTE_ALREADY_EXISTS,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoteUpdateFailedException.class)
    public ResponseEntity<ErrorResponse> handleNoteUpdateFailed(NoteUpdateFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.NOTE_NOT_UPDATED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NoteSaveFailedException.class)
    public ResponseEntity<ErrorResponse> handleNoteSaveFailed(NoteSaveFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.NOTE_NOT_SAVED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoteDeleteFailedException.class)
    public ResponseEntity<ErrorResponse> handleNoteDeleteFailed(NoteDeleteFailedException ex)
    {
        log.error(ex.getMessage(),ex);
        ErrorResponse resp = new ErrorResponse(ErrorType.NOTE_NOT_DELETED,ex.getMessage());
        return new ResponseEntity<>(resp,HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
