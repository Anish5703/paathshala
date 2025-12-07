package com.paathshala.repository;

import com.paathshala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    User findByUsername(String username);
    User findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username , String email);

}
