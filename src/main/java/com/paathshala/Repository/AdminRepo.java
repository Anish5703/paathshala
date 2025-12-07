package com.paathshala.Repository;

import com.paathshala.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepo extends JpaRepository {

    Admin findByEmail(String email);
    List<Admin> findByUsername(String username);
}
