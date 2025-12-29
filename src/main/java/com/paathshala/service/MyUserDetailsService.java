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

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername( String username)
    {
        User user = userRepo.findByUsername(username);
        if(user == null)
        {
            throw new UsernameNotFoundException("NO user found with username : "+username);
        }
        return new UserPrincipal(user);

    }
}
