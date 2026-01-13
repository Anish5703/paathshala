package com.paathshala.dto.course;

import com.paathshala.dto.ApiMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

    // Optional: Include the data if successful
    private int id;
    private String title;
    private String categoryTitle;
    private double price;
    private String description;
    @Schema(name="idPublished")
    private boolean isPublished;
    private int estimatedTime;

    private ApiMessage message;


    // Constructor for quick error responses
    public CourseResponse(String title,ApiMessage message, boolean isError) {
        this.title = title;
        this.message = message;

    }

    public CourseResponse(int id, String title, String categoryTitle, double price, String description, boolean isPublished, int estimatedTime) {
        this.id = id;
        this.title = title;
        this.categoryTitle = categoryTitle;
        this.price = price;
        this.description = description;
        this.isPublished = isPublished;
        this.estimatedTime = estimatedTime;
    }
}
