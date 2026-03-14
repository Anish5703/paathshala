package com.paathshala.dto.mcq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqQuestionResponse {

    private Integer id;
    private String courseTitle;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;  // null when serving to students before submission
    private String difficulty;
    private String explanation;

    public static McqQuestionResponse forAdmin(
            Integer id, String courseTitle, String question,
            String optionA, String optionB, String optionC, String optionD,
            String correctAnswer, String difficulty, String explanation) {
        return new McqQuestionResponse(id, courseTitle, question,
                optionA, optionB, optionC, optionD,
                correctAnswer, difficulty, explanation);
    }
    public static McqQuestionResponse forStudent(
            Integer id, String courseTitle, String question,
            String optionA, String optionB, String optionC, String optionD,
            String difficulty) {
        return new McqQuestionResponse(id, courseTitle, question,
                optionA, optionB, optionC, optionD,
                null, difficulty, null);
    }
}
