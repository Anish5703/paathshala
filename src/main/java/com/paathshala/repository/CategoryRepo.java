package com.paathshala.repository;

import com.paathshala.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category,Integer> {

    Optional<Category> findById(int id);
    Optional<Category> findByTitle(String title);
}
