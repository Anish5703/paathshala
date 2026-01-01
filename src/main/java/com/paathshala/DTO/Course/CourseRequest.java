package com.paathshala.DTO.Course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Category ID is required")
    private int categoryId;

    private double price;
    private String description;
    private boolean isPublished;
    private int estimatedTime;
}
