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

    /**
     * GET /api/admin/stats
     * Returns overall platform statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(analyticsService.getAdminStats());
    }

    /**
     * GET /api/admin/enrollments/recent?limit=10
     * Returns recent enrollment activity
     */
    @GetMapping("/enrollments/recent")
    public ResponseEntity<List<RecentEnrollmentResponse>> getRecentEnrollments(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(analyticsService.getRecentEnrollments(limit));
    }

    /**
     * GET /api/admin/courses/enrollment-count
     * Returns per-course enrollment counts
     */
    @GetMapping("/courses/enrollment-count")
    public ResponseEntity<List<CourseEnrollmentCount>> getCourseEnrollmentCounts() {
        return ResponseEntity.ok(analyticsService.getCourseEnrollmentCounts());
    }

    /**
     * GET /api/admin/revenue/summary
     * Returns revenue breakdown with monthly data
     */
    @GetMapping("/revenue/summary")
    public ResponseEntity<RevenueSummaryResponse> getRevenueSummary() {
        return ResponseEntity.ok(analyticsService.getRevenueSummary());
    }
}