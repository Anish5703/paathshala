package com.paathshala.entity;

import com.paathshala.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_tbl")
public class Student extends User {


    public Student(String username, String email , String password, Role role)
  {
      super(username, email, password, role);
  }
  public Student()
  {
      super();
  }


}
