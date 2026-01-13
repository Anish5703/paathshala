package com.paathshala.dto.course;

import com.paathshala.dto.category.CategoryDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetails {
    private String title;
    private String categoryName;
    private double price;
    private String description;
    private boolean isPublished;
    private int estimatedTime;
}
