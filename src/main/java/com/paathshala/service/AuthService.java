package com.paathshala.service;


import com.paathshala.dto.login.LoginRequest;
import com.paathshala.dto.login.LoginResponse;
import com.paathshala.dto.register.RegisterRequest;
import com.paathshala.dto.register.RegisterResponse;
import com.paathshala.entity.Admin;
import com.paathshala.entity.Student;
import com.paathshala.entity.User;
import com.paathshala.exception.auth.*;
import com.paathshala.mapper.UserMapper;
import com.paathshala.model.Role;
import com.paathshala.model.Token;
import com.paathshala.repository.AdminRepo;
import com.paathshala.repository.StudentRepo;
import com.paathshala.repository.TokenRepo;
import com.paathshala.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    //Method to login user with credentials
    public LoginResponse loginUser(LoginRequest req)
    {
        try{
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(),req.getPassword()));
            User user = userRepo.findByUsernameOrEmail(req.getUsername(),req.getUsername())
                    .orElseThrow(
                            ()-> new UsernameNotFoundException("User not found with username or email : "+req.getUsername())
                    );
            String message = "Login Successful";
            String token = jwtService.generateToken(user.getUsername());
            return UserMapper.toLoginResponse(user,token,message);
        }
  catch(AuthenticationException ex)
        {
            throw new LoginFailedException("Credentials didn't matched");

        }
    }




    //Method to register new user in database with encoding raw password
    public RegisterResponse registerUser(RegisterRequest req, HttpServletRequest servletRequest) {

        //Validating request for email and username
        RegisterResponse response = validateRegisterRequest(req);
        //Processing Valid request
        User user = UserMapper.toEntity(req) ;
        try{
            User newUser =  null;
            if(req.getRole().equals(Role.STUDENT)) {
                Student student = (Student) user;
                student.setPassword(encoder.encode(student.getPassword()));
                newUser = studentRepo.save(student);
            }
            else if(req.getRole().equals(Role.ADMIN)) {
                Admin admin = (Admin) user;
                admin.setPassword(encoder.encode(admin.getPassword()));
                 newUser = adminRepo.save(admin);
            }


            //prepare response to send
            response.setMessage("Check mail for confirmation link");
            response.setRole(req.getRole());
            //send confirmation token to the user email address
            sendConfirmationToken(newUser,servletRequest);
            return response;
        }
        catch(Exception e)
        {
           throw new RegistrationFailedException("Failed to register new user");
        }



    }

    //Method that validates the request and provides the response
    public RegisterResponse validateRegisterRequest(RegisterRequest req) {
        RegisterResponse resp = new RegisterResponse();
        resp.setUsername(req.getUsername());
        resp.setEmail(req.getEmail());
        log.info("Username : {} exists = {}",req.getUsername(),userRepo.existsByUsername(req.getUsername()));
        if (userRepo.existsByUsername(req.getUsername())) {

           throw new DuplicateUsernameFoundException("Username already exists");
        }
        if (isEmailExists(req.getEmail())) {
            throw new DuplicateEmailFoundException("Email already exists");

        }
        return resp;
    }


    //Method to validate token and Set Users Active flag true
    public RegisterResponse validateRegisterConfirmation(String tokenName)
    {
        Token token = tokenRepo.findByTokenName(tokenName);
        if(token!=null)
        {
            User user = token.getUser();
            user.setStatus(true);
            userRepo.save(user);
            return UserMapper.toRegisterResponse(token.getUser(),"Registration Successful");

        }
        else
        {
          throw new RegistrationFailedException("Failed to add user");
        }
    }

    //Method to send registration confirmation token
    public void sendConfirmationToken(User user,HttpServletRequest servletRequest) throws MessagingException
    {
        //generating token and storing it to the repo with username
        String token = generateToken();
        tokenRepo.save(new Token(token,user));

        //Concating url and token
        String confirmationLink = getConfirmationUrl(servletRequest)+token;

        //sending confirmation mail to the user
        String htmlContent = emailService.buildConfirmationEmail(user.getUsername(),confirmationLink);
        emailService.sendHtmlEmail(user.getEmail(),"Confirmation Mail",htmlContent);
    }

    //Method to resend registration confirmation token
    public RegisterResponse resendConfirmationToken(String email,HttpServletRequest servletRequest)
    {

        if(email.isEmpty())
        {
            throw new ValidationFailedException("Email required");
        }
        else {
               User user = userRepo.findByEmail(email);
               if(user==null)
               {
                   throw new RegistrationFailedException("Fill the registration form first with this email id");
               }
               else if(user.getStatus())
               {
                   throw new RegistrationFailedException("User already registered");
               }
               else
               {
                   try {
                       //deleting old token from the database
                       tokenRepo.delete(tokenRepo.findByUser(user));
                       //resending confirmation token
                       sendConfirmationToken(user, servletRequest);
                       String message = "Check mail for confirmation link";
                       return new RegisterResponse(user.getUsername(), user.getEmail(), user.getRole(),message);
                   }
                   catch(MessagingException e)
                   {
                       throw new RegistrationFailedException("Something went wrong : Failed to send confirmation link");
                   }
               }
        }

    }




    //Method to check if email exists in database
    public boolean isEmailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }




    //Method to generate confirmation URL excluding token
    public String getConfirmationUrl(HttpServletRequest request)
    {
       return  "http://"+request.getServerName()+":"+request.getServerPort()+"/api/auth/confirmRegistration?token=";
    }

    //Method to generate random token
    public String generateToken()
    {
        String token = UUID.randomUUID().toString();
        while(tokenRepo.findByTokenName(token)!=null)
        {
            token = UUID.randomUUID().toString();
        }
        return token;
    }

}