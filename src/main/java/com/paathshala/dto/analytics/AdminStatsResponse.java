package com.paathshala.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsResponse {

    private long totalUsers;
    private long totalCourses;
    private long activeStudents;   // distinct usernames in enrollments
    private long totalEnrollments;
    private double totalRevenue;   // sum of course.price where enrollment.paid = true
    private long totalCategories;

}