package com.paathshala.controller;

import com.paathshala.DTO.Category.CategoryRequest;
import com.paathshala.DTO.Category.CategoryResponse;
import com.paathshala.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request)
    {
        CategoryResponse response = categoryService.addCategory(request);
        HttpHeaders header= new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isError())
            return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> modifyCategory(@Valid @RequestBody CategoryRequest request)
    {
        CategoryResponse response = categoryService.editCategory(request);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isError())
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);
    }


    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> deleteCategory(@RequestParam int id)
    {
        CategoryResponse response = categoryService.removeCategory(id);
        HttpHeaders header= new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isError())
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);

    }

}
