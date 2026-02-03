package com.paathshala.dto.register;

import com.paathshala.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterResponse {

    private String username;
    private String email;
    private Role role;
    private String message;
}
