package com.paathshala.dto.course;

import com.paathshala.dto.category.CategoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

    // Optional: Include the data if successful
    private int id;
    private String title;
    private CategoryResponse category;
    private double price;
    private String description;
    @Schema(name="idPublished")
    private boolean isPublished;
    private int estimatedTime;

    private Map<String,Object> message;
    private boolean isError;


    // Constructor for quick error responses
    public CourseResponse(String title,Map<String,Object> message, boolean isError) {
        this.title = title;
        this.message = message;
        this.isError = isError;

    }

    public CourseResponse(int id, String title, CategoryResponse category, double price, String description, boolean isPublished, int estimatedTime) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.description = description;
        this.isPublished = isPublished;
        this.estimatedTime = estimatedTime;
    }
}
