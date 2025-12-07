package com.paathshala.entity;


import com.paathshala.model.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_tbl")
public abstract class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(unique=true,nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isActive;

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
        this.isActive = false;
    }

    //Domain Method
    public void setStatus(boolean status) {isActive = status;}
    public boolean getStatus(){return isActive;}


}