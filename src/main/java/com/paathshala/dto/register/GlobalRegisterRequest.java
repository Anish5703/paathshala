package com.paathshala.dto.register;

import com.paathshala.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GlobalRegisterRequest {

    @NotBlank(message = "Username required")
    private String username;

    @Email(message = "Valid email format required")
    @NotBlank(message = "Email required")
    private String email;

    private Role role;
}
