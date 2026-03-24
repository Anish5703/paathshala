package com.paathshala.service;

import com.paathshala.dto.user.BasicUserResponse;
import com.paathshala.dto.user.PremiumUserResponse;
import com.paathshala.dto.user.UserEnrolledCourseDTO;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.User;
import com.paathshala.repository.EnrollmentRepo;
import com.paathshala.repository.McqAttemptHistoryRepo;
import com.paathshala.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepo userRepository;
    private final EnrollmentRepo enrollmentRepository;
    private final McqAttemptHistoryRepo mcqAttemptHistoryRepository;

    /**
     * Basic Users = students with enrollments but none paid,
     *             + students with zero enrollments
     */
    public List<BasicUserResponse> getBasicUsers() {
        List<BasicUserResponse> result = new ArrayList<>();

        // 1. Students enrolled but never paid
        List<String> basicEnrolledUsernames = enrollmentRepository.findBasicEnrolledUsernames();
        for (String username : basicEnrolledUsernames) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) continue;

            List<Enrollment> enrollments = enrollmentRepository.findByUserUsername(username);
            List<UserEnrolledCourseDTO> courses = enrollments.stream()
                    .map(e -> new UserEnrolledCourseDTO(
                            e.getCourse().getTitle(),
                            e.isPaid(),
                            e.getCourse().getPrice(),
                            e.getCreatedTime().atStartOfDay() // using Enrollment.createdTime
                    )).collect(Collectors.toList());

            double avgScore = mcqAttemptHistoryRepository.getAverageScoreByUsername(username);
            result.add(new BasicUserResponse(username, user.getEmail(), courses, avgScore));
        }

        // 2. Students with no enrollments at all
        List<User> noEnrollmentStudents = userRepository.findStudentsWithNoEnrollments();
        for (User user : noEnrollmentStudents) {
            double avgScore = mcqAttemptHistoryRepository.getAverageScoreByUsername(user.getUsername());
            result.add(new BasicUserResponse(user.getUsername(), user.getEmail(), new ArrayList<>(), avgScore));
        }

        return result;
    }

    /**
     * Premium Users = students with at least one paid enrollment
     */
    public List<PremiumUserResponse> getPremiumUsers() {
        List<PremiumUserResponse> result = new ArrayList<>();

        List<String> premiumUsernames = enrollmentRepository.findPremiumUsernames();
        for (String username : premiumUsernames) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) continue;

            List<Enrollment> enrollments = enrollmentRepository.findByUserUsername(username);
            List<UserEnrolledCourseDTO> courses = enrollments.stream()
                    .filter(Enrollment::isPaid)
                    .map(e -> new UserEnrolledCourseDTO(
                            e.getCourse().getTitle(),
                            true,
                            e.getCourse().getPrice(),
                            e.getCreatedTime().atStartOfDay()
                    )).collect(Collectors.toList());

            double totalPaid = enrollments.stream()
                    .filter(Enrollment::isPaid)
                    .mapToDouble(e -> e.getCourse().getPrice())
                    .sum();

            double avgScore = mcqAttemptHistoryRepository.getAverageScoreByUsername(username);
            result.add(new PremiumUserResponse(username, user.getEmail(), courses, totalPaid, avgScore));
        }

        return result;
    }
}