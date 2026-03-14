package com.paathshala.dto.mcq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqAttemptResult {
    private Integer questionId;
    private boolean correct;
    private String correctAnswer;
    private String selectedAnswer;
    private String explanation;
}
