package com.paathshala.repository;

import com.paathshala.entity.Course;
import com.paathshala.entity.ModelQuestion;
import com.paathshala.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface ModelQuestionRepo extends JpaRepository<ModelQuestion,Integer> {

    Optional<List<ModelQuestion>> findByCourse(Course course);
    Optional<ModelQuestion> findByTitle(String modelQuestionTitle);
    Optional<ModelQuestion> findByTitleAndCourse(String modelQuestionTitle,Course course);
    boolean existsByTitleAndCourse(String modelQuestionTitle,Course course);
}
