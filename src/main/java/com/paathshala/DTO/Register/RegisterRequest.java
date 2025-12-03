package com.paathshala.DTO.Register;

import com.paathshala.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {

 @NotBlank(message = "Username required")
private String username;

 @NotBlank(message = "Password required")
 @Size(min=5 ,message="Passowrd must be atleast 5 characters")
private String password;

 @Email(message = "Valid email format requiresd")
 private String email;

 private Role role;

}
