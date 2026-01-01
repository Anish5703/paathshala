package com.paathshala.service;

import com.paathshala.DTO.Category.CategoryRequest;
import com.paathshala.DTO.Category.CategoryResponse;
import com.paathshala.entity.Category;
import com.paathshala.mapper.CategoryMapper;
import com.paathshala.repository.CategoryRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CategoryMapper categoryMapper;

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
    public CategoryResponse editCategory(CategoryRequest request)
    {
        Optional<Category> category = categoryRepo.findById(request.getId());
        Map<String,Object> message = new HashMap<>();
        if(category.isEmpty())
        {
           message.put("status","No Category found with id:"+request.getId());
           category.get().setId(request.getId());
           return categoryMapper.toCategoryResponse(category.get(),true,message);
        }
        Category modifiedCategory = categoryMapper.toEntity(request);
        Optional<Category> savedCategory = categoryRepo.findByTitle(request.getTitle());

        if(savedCategory.isPresent() && savedCategory.get().getId() != request.getId())
        {
            message.put("status","Category title duplication");
            return categoryMapper.toCategoryResponse(modifiedCategory,true,message);
        }
        Category updatedCategory = categoryRepo.save(modifiedCategory);
        message.put("status","Modified successfully");
        return categoryMapper.toCategoryResponse(updatedCategory,false,message);

    }

    @Transactional
    public CategoryResponse removeCategory(int id)
    {
        Optional<Category> category = categoryRepo.findById(id);
        Map<String,Object> message = new HashMap<>();
        if(category.isEmpty())
        {
            message.put("status","No category found");
            Category dummy = new Category();
            dummy.setId(id);
            return categoryMapper.toCategoryResponse(dummy,true,message);
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
