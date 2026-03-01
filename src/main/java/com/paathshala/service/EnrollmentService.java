package com.paathshala.service;

import com.paathshala.dto.enrollment.EnrollmentRequest;
import com.paathshala.dto.enrollment.EnrollmentResponse;
import com.paathshala.entity.Course;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.User;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.exception.enrollment.EnrollmentFailedException;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.EnrollmentRepo;
import com.paathshala.repository.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepo enrollmentRepo;

    private final UserRepo userRepo;

    private final CourseRepo courseRepo;

    public EnrollmentService(EnrollmentRepo enrollmentRepo,UserRepo userRepo,CourseRepo courseRepo)
    {
        this.enrollmentRepo = enrollmentRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
    }

    /*
      Enroll user in a FREE course (price <= 0)
      Validates:
      - User exists
      - Course exists
      - Course is free (price <= 0)
      - User not already enrolled
     */
    @Transactional
    public EnrollmentResponse enrollFree(EnrollmentRequest request) {
        // 1. Validate user
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + request.getUsername()));

        // 2. Validate course
        Course course = courseRepo.findByTitle(request.getCourseTitle())
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found: " + request.getCourseTitle()));

        // 3. Check course is free
        if (course.getPrice() > 0) {
            throw new EnrollmentFailedException(
                    "This is a paid course. Use payment checkout instead.");
        }

        // 4. Check duplicate enrollment
        if (enrollmentRepo.existsByUserUsernameAndCourseTitle(
                request.getUsername(), request.getCourseTitle())) {
            throw new EnrollmentFailedException(
                    "Already enrolled in this course.");
        }

        // 5. Create enrollment (paid=false, no stripeSessionId)
        Enrollment enrollment = new Enrollment(user, course, false, null);
        Enrollment saved = enrollmentRepo.save(enrollment);

        return new EnrollmentResponse(
                saved.getId(),
                user.getUsername(),
                course.getTitle(),
                "Successfully enrolled in " + course.getTitle()
        );
    }

    /*
      Create enrollment after verified payment
      Called by PaymentService after Stripe verification
     */
    @Transactional
    public Enrollment createPaidEnrollment(String username, String courseTitle, String stripeSessionId) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + courseTitle));

        // Prevent duplicate enrollment
        if (enrollmentRepo.existsByUserUsernameAndCourseTitle(username, courseTitle)) {
            throw new EnrollmentFailedException("Already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment(user, course, true, stripeSessionId);
        return enrollmentRepo.save(enrollment);
    }


    public List<EnrollmentResponse> getUserEnrollments(String username) {
        // Validate user exists
        if (!userRepo.existsByUsername(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
      User user = userRepo.findByUsername(username).orElseThrow(
              () -> new UsernameNotFoundException("User doesn't exists")
      );
        return enrollmentRepo.findByUser(user)
                .stream()
                .map(e -> new EnrollmentResponse(
                        e.getId(),
                        e.getUser().getUsername(),
                        e.getCourse().getTitle(),
                        e.isPaid() ? "Paid enrollment" : "Free enrollment"
                ))
                .toList();
    }
}
