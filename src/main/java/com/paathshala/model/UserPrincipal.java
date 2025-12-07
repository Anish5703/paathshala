package com.paathshala.model;

import com.paathshala.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user)
    {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()));
    }

    @Override
    public String getUsername()
    {
        return user.getUsername();
    }

    @Override
    public String getPassword()
    {
        return user.getPassword();
    }

    @Override
    public boolean isEnabled(){return user.isActive();}


}
