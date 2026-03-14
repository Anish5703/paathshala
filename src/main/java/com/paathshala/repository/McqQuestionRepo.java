package com.paathshala.repository;

import com.paathshala.entity.McqQuestion;
import com.paathshala.model.Difficulty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface McqQuestionRepo extends JpaRepository<McqQuestion,Integer> {

    List<McqQuestion> findByCourse_Id(Integer courseId);
    List<McqQuestion> findByCourse_IdAndDifficulty(Integer courseId, Difficulty difficulty);
    @Query("SELECT m FROM McqQuestion m WHERE m.course.title = :courseTitle AND m.difficulty = :difficulty ORDER BY FUNCTION('RANDOM')")
    List<McqQuestion> findRandomByCourseAndDifficulty(
            @Param("courseTitle") String courseTitle,
            @Param("difficulty") Difficulty difficulty,
            Pageable pageable
    );
    long countByCourse_IdAndDifficulty(Integer courseId, Difficulty difficulty);
}
