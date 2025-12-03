package com.paathshala.DTO.Login;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message="Email required")
    private String email;

    @NotBlank(message="Password required")
    private String password;
}
