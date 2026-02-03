package com.paathshala.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Category ID is required")
    private String categoryTitle;

    private double price;
    private String description;
    @Schema(name = "isPublished")
    private boolean isPublished;
    private int estimatedTime;

    private String imageUrl;

    public CourseRequest(String title, String categoryTitle, double price, String description, boolean isPublished, int estimatedTime,String imageUrl) {
        this.title = title;
        this.categoryTitle = categoryTitle;
        this.price = price;
        this.description = description;
        this.isPublished = isPublished;
        this.estimatedTime = estimatedTime;
        this.imageUrl = imageUrl;
    }
}
