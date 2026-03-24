package com.paathshala.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEnrolledCourseDTO {
    private String courseTitle;
    private boolean paid;
    private double price;
    private LocalDateTime enrolledAt;
}