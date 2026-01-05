package com.paathshala.DTO.Course;

import com.paathshala.DTO.Category.CategoryDetails;
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
