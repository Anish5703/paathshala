package com.paathshala.entity;

import com.paathshala.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_tbl")
public class Student extends User {

  private boolean isActive;


    public Student(String username, String email , String password, Role role)
  {
      super(username, email, password, role);
      isActive = false;
  }
  public Student()
  {
      super();
      isActive = false;
  }

  public void setStatus(boolean status) {isActive = status;}
    public boolean getStatus(){return isActive;}
}
