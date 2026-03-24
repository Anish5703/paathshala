package com.paathshala.service;

import com.paathshala.dto.analytics.*;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.McqAttemptHistory;
import com.paathshala.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentAnalyticsService {

    private final EnrollmentRepo enrollmentRepository;
    private final McqAttemptHistoryRepo mcqAttemptHistoryRepository;

    public StudentAnalyticsService(
            EnrollmentRepo enrollmentRepository,
            McqAttemptHistoryRepo mcqAttemptHistoryRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.mcqAttemptHistoryRepository = mcqAttemptHistoryRepository;
    }

    /**
     * Get student's personal learning statistics
     */
    public StudentStatsResponse getStudentStats(String username) {
        int enrolled = enrollmentRepository.countByUserUsername(username);
        int mcqAttempts = mcqAttemptHistoryRepository.countByUsername(username);
        double avgScore = mcqAttemptHistoryRepository.getAverageScoreByUsername(username);
        int paidCourses = enrollmentRepository.countByUserUsernameAndPaid(username, true);

        return new StudentStatsResponse(
                enrolled,
                mcqAttempts,
                (int) avgScore,  // cast double to int
                paidCourses
        );
    }

    /**
     * Get student's enrolled courses
     */
    public List<StudentCourseResponse> getStudentCourses(String username) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserUsername(username);

        return enrollments.stream()
                .map(e -> {
                    var course = e.getCourse();
                    return new StudentCourseResponse(
                            course.getId(),
                            course.getTitle(),
                            course.getCategory() != null ? course.getCategory().getTitle() : null,
                            course.getImageUrl(),
                            e.isPaid(),
                            course.getPrice(),
                            e.getCreatedTime().atStartOfDay()   // renamed from enrolledAt
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get student's MCQ attempt history
     */
    public List<McqAttemptHistoryResponse> getMcqHistory(String username, String courseTitle) {
        List<McqAttemptHistory> attempts;
        if (courseTitle != null && !courseTitle.isBlank()) {
            attempts = mcqAttemptHistoryRepository
                    .findByUsernameAndCourse_TitleOrderByAttemptedAtDesc(username, courseTitle);
        } else {
            attempts = mcqAttemptHistoryRepository
                    .findByUsernameOrderByAttemptedAtDesc(username);
        }

        return attempts.stream()
                .map(a -> new McqAttemptHistoryResponse(
                        a.getId(),
                        a.getCourse().getTitle(),
                        a.getDifficulty(),  // String field
                        a.getTotalQuestions(),
                        a.getCorrectAnswers(),
                        a.getScore(),
                        a.getAttemptedAt()
                ))
                .collect(Collectors.toList());
    }
}