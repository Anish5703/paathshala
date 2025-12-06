package com.paathshala.repository;

import com.paathshala.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepo extends JpaRepository <Student,Integer> {

    Optional<Student> findByEmail(String email);
    Optional<Student> findByUsername(String username);
}
