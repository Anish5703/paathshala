package com.paathshala.service;

import com.paathshala.DTO.Category.CategoryDetails;
import com.paathshala.DTO.Category.CategoryRequest;
import com.paathshala.DTO.Category.CategoryResponse;
import com.paathshala.DTO.Course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import com.paathshala.mapper.CategoryMapper;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.repository.CategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CourseMapper courseMapper;

    public List<CategoryDetails> getAllCategory()
    {
        List<Category>  categories = categoryRepo.findAll();
        if(categories.isEmpty())
            return Collections.emptyList();
      return categoryMapper.toCategoryDetailsList(categories);
    }

    @Transactional
    public List<CourseResponse> getCoursesByCategory(int categoryId)
    {
        Optional<Category> category = categoryRepo.findById(categoryId);
        List<Course> courses = category.get().getCourses();
        if(courses.isEmpty())
            return Collections.emptyList();
        else
            return courseMapper.toCourseResponseList(courses);
    }

    @Transactional
    public CategoryResponse addCategory(CategoryRequest request)
    {
        Optional<Category> category = categoryRepo.findByTitle(request.getTitle());
        Map<String,Object> message = new HashMap<>();
        if(category.isPresent())
        {
            message.put("status","Category already exists");
            return categoryMapper.toCategoryResponse(category.get(),true,message);
        }
        Category savedCategory = categoryRepo.save( categoryMapper.toEntity(request) );
        message.put("status","Category added");
        return categoryMapper.toCategoryResponse(savedCategory,false,message);
    }

    @Transactional
    public CategoryResponse editCategory(CategoryRequest request,int categoryId)
    {
        if (request==null)
            throw new IllegalArgumentException("Category cannot be null");
        Optional<Category> category = categoryRepo.findById(categoryId);
        Map<String,Object> message = new HashMap<>();
        if(category.isEmpty())
        {
           message.put("status","No Category found with id:"+categoryId);
           category.get().setId(categoryId);
           return categoryMapper.toCategoryResponse(category.get(),true,message);
        }
        Category modifiedCategory = categoryMapper.toEntity(request);
        Optional<Category> savedCategory = categoryRepo.findByTitle(request.getTitle());

        if(savedCategory.isPresent() && savedCategory.get().getId() != categoryId)
        {
            message.put("status","Category title duplication");
            return categoryMapper.toCategoryResponse(modifiedCategory,true,message);
        }
        Category updatedCategory = categoryRepo.save(modifiedCategory);
        message.put("status","Modified successfully");
        return categoryMapper.toCategoryResponse(updatedCategory,false,message);

    }

    @Transactional
    public CategoryResponse removeCategory(int categoryId)
    {
        if(categoryId<0)
            throw new IllegalArgumentException("Category cannot be null");
        Optional<Category> category = categoryRepo.findById(categoryId);
        Map<String,Object> message = new HashMap<>();
        if(category.isEmpty())
        {
            message.put("status","No category found");
            Category dummy = new Category();
            dummy.setId(categoryId);
            return categoryMapper.toCategoryResponse(dummy,true,message);
        }
        if(!category.get().getCourses().isEmpty())
        {
            message.put("status","Cannot delete the category since it has courses");
            return categoryMapper.toCategoryResponse(category.get(),true,message);
        }
        categoryRepo.delete(category.get());
        message.put("status","Category removed");
        return categoryMapper.toCategoryResponse(category.get(),false,message);
    }

    public boolean isExists(String categoryTitle)
    {
        Optional<Category> savedCategory = categoryRepo.findByTitle(categoryTitle);
        return savedCategory.isPresent();

    }
}
