package com.paathshala.DTO.Login;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message="Email required")
    private String email;

    @NotBlank(message="Password required")
    private String password;



}
