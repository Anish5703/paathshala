package com.paathshala.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatsResponse {

    private int enrolledCourses;
    private int totalMcqAttempts;
    private int averageMcqScore;
    private int paidCourses;

}