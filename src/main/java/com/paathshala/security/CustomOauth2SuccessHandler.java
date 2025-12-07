package com.paathshala.security;


import com.paathshala.entity.User;
import com.paathshala.repository.UserRepo;
import com.paathshala.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOauth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OauthService oauthService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;





    @Override
    public void onAuthenticationSuccess(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Authentication authentication) throws
            IOException, ServletException
    {

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            //fetching email from google success authentication
            String email = oAuth2User.getAttribute("email");

            boolean emailExists = oauthService.isEmailExists(email);
            DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

            //if email is empty redirecting to login page
            if (email == null)
            {
                String url = "/api/auth/login";
                redirectStrategy.sendRedirect(servletRequest, servletResponse, url);
                return;
            }
            //if email exists in database then setting jwt cookie and redirecting to oauth home controller
            else if (emailExists)
            {
                User user = userRepo.findByEmail(email);
                oauthService.setJwtCookie(user.getUsername(),servletResponse,servletRequest);
                String url = "/api/oauth/home";
                redirectStrategy.sendRedirect(servletRequest, servletResponse, url);
                return;
            }
            //if email doesnot exists in database then setting email cookie and redirecting to oath register controller
            else
            {
                oauthService.setEmailCookie(email,servletResponse,servletRequest);
                String url = "/api/oauth/register";
                redirectStrategy.sendRedirect(servletRequest, servletResponse, url);
                return;
            }
        }





}
