package com.paathshala.dto.course;

import com.paathshala.dto.category.CategoryDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetails {
    private int id;
    private String title;
    private CategoryDetails category;
    private double price;
    private String description;
    private boolean isPublished;
    private int estimatedTime;
}
