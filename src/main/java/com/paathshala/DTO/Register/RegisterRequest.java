package com.paathshala.DTO.Register;

import com.paathshala.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest extends GlobalRegisterRequest{


 @NotBlank(message = "Password required")
 @Size(min=5 ,message="Password must be at least 5 characters")
private String password;

 public RegisterRequest(String username,String password,String email,Role role)
 {
  super(username, email, role);
  this.password = password;
 }



}
