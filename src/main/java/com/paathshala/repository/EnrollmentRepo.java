package com.paathshala.repository;

import com.paathshala.entity.Course;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EnrollmentRepo extends JpaRepository<Enrollment,Integer> {

    List<Enrollment> findByUser(User user);
    Enrollment findByUserAndCourse(User user, Course course);
    List<Enrollment> findByCourse(Course course);

    boolean existsByUserUsernameAndCourseTitle(String username, String courseTitle);

    @Query("""
           SELECT COUNT(DISTINCT e.user.id)
           FROM Enrollment e
           """)
    long totalActiveUsers();

}
