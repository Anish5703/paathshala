package com.paathshala.repository;

import com.paathshala.entity.McqAttemptHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface McqAttemptHistoryRepo extends JpaRepository<McqAttemptHistory,Integer> {

    List<McqAttemptHistory> findByUsernameOrderByAttemptedAtDesc(String username);

    List<McqAttemptHistory> findByUsernameAndCourse_TitleOrderByAttemptedAtDesc(String username, String courseTitle);

    List<McqAttemptHistory> findByUsernameAndCourse_IdOrderByAttemptedAtDesc(String username, Integer courseId);

    Optional<McqAttemptHistory> findFirstByUsernameAndCourse_IdOrderByAttemptedAtDesc(String username, Integer courseId);

    int countByUsername(String username);

    @Query("""
           SELECT COALESCE(AVG(m.score), 0)
           FROM McqAttemptHistory m
           WHERE m.username = :username
           """)
    double getAverageScoreByUsername(String username);

    @Query("""
           SELECT COALESCE(SUM(m.score), 0)
           FROM McqAttemptHistory m
           WHERE m.username = :username
           """)
    double getTotalScoreByUsername(String username);

    @Query("""
           SELECT COALESCE(AVG(m.score), 0)
           FROM McqAttemptHistory m
           WHERE m.username = :username AND m.course.id = :courseId
           """)
    double getAverageScoreByUsernameAndCourse(String username, Integer courseId);
}