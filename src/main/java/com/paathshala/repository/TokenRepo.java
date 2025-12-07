package com.paathshala.repository;

import com.paathshala.model.Token;
import com.paathshala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<Token,Integer> {

    Token findByTokenName(String tokenName);
    Token findByUser(User user);

}
