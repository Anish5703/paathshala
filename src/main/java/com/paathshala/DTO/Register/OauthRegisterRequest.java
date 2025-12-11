package com.paathshala.DTO.Register;

import com.paathshala.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OauthRegisterRequest extends RegisterRequest{
    @NotBlank(message = "Username required")
    private String username;

    @Email(message = "Valid email format required")
    @NotBlank(message = "Email required")
    private String email;

    private Role role;
}
