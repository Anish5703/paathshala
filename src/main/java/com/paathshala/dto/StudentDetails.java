package com.paathshala.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDetails {
    private int id;
    private String username;
    private String email;
    private boolean isActive;
}
