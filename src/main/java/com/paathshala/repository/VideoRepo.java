package com.paathshala.repository;

import com.paathshala.entity.Course;
import com.paathshala.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepo extends JpaRepository<Video,Integer> {

    Optional<List<Video>> findByCourse(Course course);
    Optional<Video> findByTitle(String videoTitle);
    Optional<Video> findByTitleAndCourse(String videoTitle,Course course);
    boolean existsByTitleAndCourse(String videoTitle,Course course);

}
