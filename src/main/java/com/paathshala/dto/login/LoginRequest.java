package com.paathshala.dto.login;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message="Username or Email required")
    private String username;

    @NotBlank(message="Password required")
    private String password;



}
