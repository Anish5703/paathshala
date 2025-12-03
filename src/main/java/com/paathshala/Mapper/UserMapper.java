package com.paathshala.Mapper;


import com.paathshala.DTO.Login.LoginRequest;
import com.paathshala.DTO.Login.LoginResponse;
import com.paathshala.DTO.Register.RegisterRequest;
import com.paathshala.DTO.Register.RegisterResponse;
import com.paathshala.entity.Role;
import com.paathshala.entity.User;

import java.util.Map;

//Mapper between Entity and DTOs


public class UserMapper {

    /**
     * Converts {@link RegisterRequest} to {@link User}
     * @param req the registration request DTO containing user input
     * @return a {@link User} entity
     */
    public static User toEntity(RegisterRequest req)
    {
        return new User(
                req.getUsername(),
                req.getEmail(),
                req.getPassword(),
                req.getRole()
        );
    }

   //User Entity to LoginResponse DTO
    public static LoginResponse toLoginResponse(User user, String token, Map<String,Object> message, boolean error)
    {
        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                token,
                message,
                error
        );
    }

    //LoginRequest to loginResponse DTO
    public static LoginResponse toLoginResponse(LoginRequest request,Map<String,Object> message,boolean error)
    {
        return new LoginResponse(
                null,
                request.getEmail(),
                null,
                null,
                message,
                error
        );
    }

    //User Entity to RegisterResponse DTO
    public static RegisterResponse toRegisterResponse(User user,Map<String,Object> message,boolean error)
    {
         return new RegisterResponse(
                 user.getUsername(),
                 user.getEmail(),
                 user.getRole(),
                 message,
                 error
         );
    }
}
