package com.paathshala.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int Id;

    @Column(nullable = false)
    private String tokenName;

    @ManyToOne
    @JoinColumn(name="users_id",nullable=false)
    private User user;


    public Token(String tokenName, User user) {
        this.tokenName = tokenName;
        this.user = user;
    }
}
