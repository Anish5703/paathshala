package com.paathshala.service;

import com.paathshala.dto.analytics.*;
import com.paathshala.entity.Enrollment;
import com.paathshala.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    private final UserRepo userRepository;
    private final CourseRepo courseRepository;
    private final CategoryRepo categoryRepository;
    private final EnrollmentRepo enrollmentRepository;

    public AdminAnalyticsService(
            UserRepo userRepository,
            CourseRepo courseRepository,
            CategoryRepo categoryRepository,
            EnrollmentRepo enrollmentRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Get overall platform statistics
     * Revenue is calculated from enrollment.course.price where paid = true
     */
    public AdminStatsResponse getAdminStats() {
        return new AdminStatsResponse(
                userRepository.count(),
                courseRepository.count(),
                enrollmentRepository.countActiveStudents(),
                enrollmentRepository.count(),
                enrollmentRepository.getTotalRevenue(),
                categoryRepository.count()
        );
    }

    /**
     * Get recent enrollment activity
     */
    public List<RecentEnrollmentResponse> getRecentEnrollments(int limit) {
        List<Enrollment> enrollments = enrollmentRepository
                .findRecentEnrollments(PageRequest.of(0, limit));

        return enrollments.stream()
                .map(e -> new RecentEnrollmentResponse(
                        e.getId(),
                        e.getUser().getUsername(),
                        e.getCourse().getTitle(),
                        e.isPaid(),
                        e.getCreatedTime().atStartOfDay()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get per-course enrollment counts
     */
    public List<CourseEnrollmentCount> getCourseEnrollmentCounts() {
        return enrollmentRepository.countEnrollmentsByCourse().stream()
                .map(row -> new CourseEnrollmentCount(
                        (String) row[0],
                        (Long) row[1]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get revenue summary with monthly breakdown
     * All revenue data derived from EnrollmentRepository (no PaymentRepository)
     */
    public RevenueSummaryResponse getRevenueSummary() {
        double total = enrollmentRepository.getTotalRevenue();
        double thisMonth = enrollmentRepository.getThisMonthRevenue();
        double lastMonth = enrollmentRepository.getLastMonthRevenue();
        double growth = lastMonth > 0 ? ((thisMonth - lastMonth) / lastMonth) * 100 : 0;

        List<MonthlyRevenue> monthly = enrollmentRepository.getMonthlyRevenue().stream()
                .map(row -> new MonthlyRevenue(
                        (String) row[0],
                        ((Number) row[1]).doubleValue()
                ))
                .collect(Collectors.toList());

        return new RevenueSummaryResponse(total, thisMonth, lastMonth, growth, monthly);
    }
}