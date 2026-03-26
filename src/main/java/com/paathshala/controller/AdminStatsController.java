package com.paathshala.controller;

import com.paathshala.dto.analytics.*;
import com.paathshala.service.AdminAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminAnalyticsService analyticsService;

    public AdminStatsController(AdminAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }


    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(analyticsService.getAdminStats());
    }

    @GetMapping("/enrollments/recent")
    public ResponseEntity<List<RecentEnrollmentResponse>> getRecentEnrollments(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(analyticsService.getRecentEnrollments(limit));
    }


    @GetMapping("/courses/enrollment-count")
    public ResponseEntity<List<CourseEnrollmentCount>> getCourseEnrollmentCounts() {
        return ResponseEntity.ok(analyticsService.getCourseEnrollmentCounts());
    }


    @GetMapping("/revenue/summary")
    public ResponseEntity<RevenueSummaryResponse> getRevenueSummary() {
        return ResponseEntity.ok(analyticsService.getRevenueSummary());
    }
}