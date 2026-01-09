package com.paathshala.repository;

import com.paathshala.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course,Integer> {

    Optional<Course> findById(int id);
    Optional<Course> findByTitle(String title);
//    Course findByTitle(String title);

}
