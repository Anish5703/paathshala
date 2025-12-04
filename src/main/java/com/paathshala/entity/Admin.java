package com.paathshala.entity;

import com.paathshala.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_tbl")
public class Admin extends User {


}
