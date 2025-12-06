package com.paathshala.repository;

import com.paathshala.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepo extends JpaRepository<Admin,Integer>{

    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByUsername(String username);
}
