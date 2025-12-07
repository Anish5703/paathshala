package com.paathshala.repository;

import com.paathshala.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository {

    Admin findByEmail(String email);
    Admin findByUsername(String username);
    Admin save(Admin admin);
}
