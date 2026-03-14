package com.paathshala.repository;

import com.paathshala.dto.mcq.McqAttemptResult;
import com.paathshala.entity.McqAttemptHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface McqAttemptHistoryRepo extends JpaRepository<McqAttemptHistory,Integer> {

    List<McqAttemptHistory> findByUsernameAndCourse_IdOrderByAttemptedAtDesc(
            String username, Integer courseId);

    Optional<McqAttemptHistory> findFirstByUsernameAndCourse_IdOrderByAttemptedAtDesc(
            String username, Integer courseId);
}
