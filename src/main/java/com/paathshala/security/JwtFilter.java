package com.paathshala.security;


import com.paathshala.service.JwtService;
import com.paathshala.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        String username = null;

        log.info("JWT FILTER HIT: {}", req.getServletPath());

        //Authorization header (for API / Postman)
        String authHeader = req.getHeader("Authorization");
        log.info("Authorization Header: {}",authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        log.info("Jwt Token : {}",token);
        //no header, try COOKIE (OAuth flow)
        if (token == null && req.getCookies() != null) {
            for (var cookie : req.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        //Validate & authenticate
       try {
           if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               username = jwtService.extractUsername(token);

               log.info("Extracted username from token / : {}",username);

               UserDetails userDetails = userDetailsService.loadUserByUsername(username);
               log.info("Extracted UserDetails : {} {}",userDetails.getAuthorities(),userDetails.getUsername());
         boolean isTokenValid = jwtService.validateToken(token,userDetails);
         log.info("isTokenValid : {}",isTokenValid);
               if (isTokenValid) {
                   UsernamePasswordAuthenticationToken authToken =
                           new UsernamePasswordAuthenticationToken(
                                   userDetails,
                                   null,
                                   userDetails.getAuthorities()
                           );


                   authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                   SecurityContextHolder.getContext().setAuthentication(authToken);
                   log.info("GetAuthentication : {}",SecurityContextHolder.getContext().getAuthentication());
               }
           }

           filterChain.doFilter(req, resp);
           log.info("Jwt filtration completed");
       }
       catch(ExpiredJwtException e) {
           throw new ExpiredJwtException(null,null,"Jwt token is expired");
       }
       catch(SignatureException e){
           throw new SignatureException("Jwt token invalid");
       }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/note/")
                || path.startsWith("/video/")
                || path.startsWith("/modelQuestion/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

}

