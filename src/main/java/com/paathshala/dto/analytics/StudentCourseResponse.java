package com.paathshala.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseResponse {

    private Integer courseId;
    private String courseTitle;
    private String categoryTitle;
    private String imageUrl;
    private boolean paid;
    private double price;
    private LocalDateTime enrolledAt;

}