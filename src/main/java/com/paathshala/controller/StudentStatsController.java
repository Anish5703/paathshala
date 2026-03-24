package com.paathshala.controller;

import com.paathshala.dto.analytics.*;
import com.paathshala.service.StudentAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/{username}")
@PreAuthorize("isAuthenticated()")
public class StudentStatsController {

    private final StudentAnalyticsService analyticsService;

    public StudentStatsController(StudentAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /api/student/{username}/stats
     * Returns personal learning statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<StudentStatsResponse> getStats(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(analyticsService.getStudentStats(username));
    }

    /**
     * GET /api/student/{username}/courses
     * Returns enrolled courses
     */
    @GetMapping("/courses")
    public ResponseEntity<List<StudentCourseResponse>> getCourses(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(analyticsService.getStudentCourses(username));
    }

    /**
     * GET /api/student/{username}/mcq-history
     * Returns MCQ attempt history, optionally filtered by course
     */
    @GetMapping("/mcq-history")
    public ResponseEntity<List<McqAttemptHistoryResponse>> getMcqHistory(
            @PathVariable String username,
            @RequestParam(required = false) String courseTitle
    ) {
        return ResponseEntity.ok(analyticsService.getMcqHistory(username, courseTitle));
    }
}