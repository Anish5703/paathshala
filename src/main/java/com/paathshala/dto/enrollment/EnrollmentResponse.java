package com.paathshala.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrollmentResponse {
    private int id;
    private String username;
    private String courseTitle;
    private String message;
}
