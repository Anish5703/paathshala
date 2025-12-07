package com.paathshala.repository;

import com.paathshala.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository <Student,Integer> {

    Student findByEmail(String email);
    Student findByUsername(String username);
}
