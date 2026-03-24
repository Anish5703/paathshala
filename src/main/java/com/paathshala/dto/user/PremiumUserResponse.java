package com.paathshala.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumUserResponse {
    private String username;
    private String email;
    private List<UserEnrolledCourseDTO> enrolledCourses;
    private double totalPaid;
    private double averageMcqScore;
}