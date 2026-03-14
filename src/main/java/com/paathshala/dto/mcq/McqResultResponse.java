package com.paathshala.dto.mcq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqResultResponse {

    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private String nextDifficulty;
    private List<McqAttemptResult> results;
}
