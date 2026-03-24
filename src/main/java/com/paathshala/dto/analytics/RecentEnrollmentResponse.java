package com.paathshala.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentEnrollmentResponse {

    private Integer id;
    private String username;
    private String courseTitle;
    private boolean paid;
    private LocalDateTime enrolledAt;

}