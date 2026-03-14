package com.paathshala.dto.mcq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqAttempt{
    @NotNull
    private Integer questionId;

    @NotBlank
    private String selectedAnswer ;
}
