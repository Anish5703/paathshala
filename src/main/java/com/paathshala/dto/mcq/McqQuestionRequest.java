package com.paathshala.dto.mcq;

import com.paathshala.model.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqQuestionRequest {
    @NotBlank
    private String question;
    @NotBlank
    private String optionA;
    @NotBlank
    private String optionB;
    @NotBlank
    private String optionC;
    @NotBlank
    private String optionD;
    @NotBlank
    private String correctAnswer;  // "A", "B", "C", "D"
    @NotNull
    private Difficulty difficulty;

    private String explanation      ;          // optional
}
