package com.paathshala.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_tbl")
public class Student extends User{

    private boolean isActive;

}
