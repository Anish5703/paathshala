package com.paathshala.service;


import com.paathshala.entity.User;
import com.paathshala.model.UserPrincipal;
import com.paathshala.repository.UserRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername( String username)
    {
        User user = userRepo.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found")
                );
        return new UserPrincipal(user);

    }
}
