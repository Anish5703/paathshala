package com.paathshala.repository;

import com.paathshala.entity.Course;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EnrollmentRepo extends JpaRepository<Enrollment,Integer> {

    List<Enrollment> findByUser(User user);
    Enrollment findByUserAndCourse(User user, Course course);
    List<Enrollment> findByCourse(Course course);

    boolean existsByUserUsernameAndCourseTitle(String username, String courseTitle);
}
