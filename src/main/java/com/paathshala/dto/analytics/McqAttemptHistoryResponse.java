package com.paathshala.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqAttemptHistoryResponse {

    private Integer id;
    private String courseTitle;
    private String difficulty;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private LocalDateTime attemptedAt;

}