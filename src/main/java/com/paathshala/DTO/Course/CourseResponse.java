package com.paathshala.DTO.Course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

    private Map<String,Object> message;
    private boolean isError;

    // Optional: Include the data if successful
    private int id;
    private String title;

    // Standard timestamp for responses
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructor for quick error responses
    public CourseResponse(Map<String,Object> message, boolean isError) {
        this.message = message;
        this.isError = isError;
        this.timestamp = LocalDateTime.now();
    }
}
