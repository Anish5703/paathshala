package com.paathshala.controller;


import com.paathshala.dto.login.LoginResponse;
import com.paathshala.dto.register.OauthRegisterRequest;
import com.paathshala.dto.register.RegisterRequest;
import com.paathshala.dto.register.RegisterResponse;
import com.paathshala.exception.auth.LoginFailedException;
import com.paathshala.model.Role;
import com.paathshala.service.OauthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/oauth")
public class OauthController {

    @Autowired
    private OauthService oauthService;

    //Endpoint to fetch email and also redirect to set username page
    @GetMapping("/register")
    public ResponseEntity<RegisterRequest> setUsername(@CookieValue(name="email",required = false)String email)
    {
        RegisterRequest registerRequest = new RegisterRequest(null,null,email, Role.STUDENT); // harcoded role STUDENT in oauth registration
            return ResponseEntity.status(HttpStatus.OK).body(registerRequest);


    }

    //Endpoint to register user for first time oauth login
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerOauth(@RequestBody OauthRegisterRequest oauthRegisterRequest,
                                                          HttpServletResponse servletResponse,HttpServletRequest servletRequest)
    {
        String password = KeyGenerators.string().generateKey(); //generate a random password
        RegisterRequest registerRequest = new RegisterRequest(
               oauthRegisterRequest.getUsername(),password, oauthRegisterRequest.getEmail(), oauthRegisterRequest.getRole()
        );
      RegisterResponse registerResponse = oauthService.registerUser(registerRequest);
         oauthService.setJwtCookie(registerResponse.getUsername(),servletResponse,servletRequest);
          return ResponseEntity.status(HttpStatus.OK).body(registerResponse);

    }

    //home endpoint after auth login
    @GetMapping("/home")
    public ResponseEntity<LoginResponse> oauthHome(@CookieValue(name="jwt",required = false)String token)
    {
        if (token == null || token.isBlank()) {
            throw new LoginFailedException("No session detected: Login Again");
        }

        LoginResponse loginResponse = oauthService.validateToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+token);
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(loginResponse);


    }

    /*
   { Flow through oauth login }
    select account -> invoke successhandler ->
     *For previously Logged in user -> set jwt cookie -> redirected to (GET)"/api/oauth/home" -> receive json with jwt token and loginResponse;

     *For first time Logged in user -> redirected to (GET) "/api/oauth/register" -> receive json with email
                                    -> pass json with email and username -> Call (POST) "/api/oauth/register" -> set jwt cookie -> receive json with user info
                                    ->Call (GET) "/api/oath/home" -> receive json with jwt token and loginResponse

     */

    /*
    /oauth2/authorization/google -> to get options of accounts to select
     */

}
