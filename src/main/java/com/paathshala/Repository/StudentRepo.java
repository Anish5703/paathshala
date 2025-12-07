package com.paathshala.Repository;

import com.paathshala.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository <Student,Integer> {

    Student findByEmail(String email);
}
