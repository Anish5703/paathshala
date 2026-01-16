package com.paathshala.controller;

import com.paathshala.DTO.Category.CategoryDetails;
import com.paathshala.DTO.Category.CategoryRequest;
import com.paathshala.DTO.Category.CategoryResponse;
import com.paathshala.DTO.Course.CourseResponse;
import com.paathshala.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDetails>> allCategory()
    {
        List<CategoryDetails> response = categoryService.getAllCategory();
        HttpHeaders header= new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isEmpty())
            return ResponseEntity.status(HttpStatus.FOUND).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(header).body(response);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> categoryCourses(@RequestParam int categoryId)
    {
        List<CourseResponse> response = categoryService.getCoursesByCategory(categoryId);
        HttpHeaders header= new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isEmpty())
            return ResponseEntity.status(HttpStatus.FOUND).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(header).body(response);

    }

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

    @PutMapping("/edit/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> modifyCategory(@Valid @RequestBody CategoryRequest request,@PathVariable int categoryId)
    {
        CategoryResponse response = categoryService.editCategory(request,categoryId);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isError())
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);
    }

    @DeleteMapping("/remove/{id}")
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
