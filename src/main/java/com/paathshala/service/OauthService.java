package com.paathshala.service;


import com.paathshala.dto.login.LoginResponse;
import com.paathshala.dto.register.RegisterRequest;
import com.paathshala.dto.register.RegisterResponse;
import com.paathshala.entity.Admin;
import com.paathshala.entity.Student;
import com.paathshala.entity.User;
import com.paathshala.exception.auth.DuplicateUsernameFoundException;
import com.paathshala.exception.auth.LoginFailedException;
import com.paathshala.exception.auth.RegistrationFailedException;
import com.paathshala.mapper.UserMapper;
import com.paathshala.model.Role;
import com.paathshala.repository.AdminRepo;
import com.paathshala.repository.StudentRepo;
import com.paathshala.repository.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class OauthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(OauthService.class);




//Method to register user for first attempt of oauth login
    public RegisterResponse registerUser(RegisterRequest req)
    {
        boolean usernameExists = isUsernameExists(req.getUsername());
        logger.info("Username exists : {}",usernameExists);

        if(usernameExists)
        {
            throw new DuplicateUsernameFoundException("Username already exists");
        }
        else
        {
            try {
                User user = UserMapper.toEntity(req);
                user.setStatus(true);
                User newUser = null;
                if(req.getRole().equals(Role.STUDENT)) {
                    Student student =(Student) user;
                    newUser = studentRepo.save(student);
                }
                else if(req.getRole().equals(Role.ADMIN)) {
                    Admin admin = (Admin) user;
                    newUser = adminRepo.save(admin);
                }

                return UserMapper.toRegisterResponse(newUser,"Registration Successful");
            }
            catch(Exception e)
            {
                throw new RegistrationFailedException("Failed to register new user");

            }
        }
    }

    //Method to set email in cookie
    public boolean setEmailCookie(String email,HttpServletResponse servletResponse,HttpServletRequest servletRequest)
    {
        if(email == null)
            return false;
        else
        {
            boolean isProduction = !servletRequest.getServerName().equals("localhost");

            Cookie cookie = new Cookie("email", email);
            cookie.setHttpOnly(true);
            cookie.setSecure(isProduction);
            cookie.setPath("/");
            cookie.setMaxAge(1200);
            servletResponse.addCookie(cookie);
            return true;
        }
    }

    //Method to generate and set jwt in cookie
    public boolean setJwtCookie(String username, HttpServletResponse servletResponse,HttpServletRequest servletRequest)
    {
        if(username == null)
            return false;
        else
        {
            boolean isProduction = !servletRequest.getServerName().equals("localhost");

            String token = jwtService.generateToken(username);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(isProduction);
            cookie.setPath("/");
            cookie.setMaxAge(1200);
            servletResponse.addCookie(cookie);
            return true;

        }
    }

    //Method to validate and create  login response using jwt token
    public LoginResponse validateToken(String token)
    {
        String username =(String) jwtService.extractUsername(token);
        User user = userRepo.findByUsername(username);
        if(user != null)
            return UserMapper.toLoginResponse(user,token,"Login Successful");
        else
            throw new LoginFailedException("Session expired : Login again");
    }

    //Method to check if username exists in database
    public boolean isUsernameExists(String username) {
        return userRepo.findByUsername(username) != null;

    }

    //Method to check if email exists in database
    public boolean isEmailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }
}
