package com.paathshala.entity;

import com.paathshala.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "admin_tbl")
public class Admin extends User {

public Admin(String username, String email, String password, Role role)
{
    super(username, email, password, role);
}
public Admin()
{
    super();
}
}
