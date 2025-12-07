package com.paathshala.entity;


import com.paathshala.model.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_tbl")
public abstract class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public User(String username, String email , String password, Role role)
    {
        if(username == null || email == null || password == null || role == null)
            throw new IllegalArgumentException("Fields cannot be null");
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected void setUsername(String username){ this.username = username;}
    protected void setEmail(String email){ this.email = email;}
    protected void setPassword(String password){ this.password = password;}
    protected void setRole(Role role){ this.role = role;}

    //domain method
    public void changePassword(String password){setPassword(password);}


}