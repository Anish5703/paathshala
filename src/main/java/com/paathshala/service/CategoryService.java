package com.paathshala.service;

import com.paathshala.dto.ApiMessage;
import com.paathshala.dto.category.CategoryDetails;
import com.paathshala.dto.category.CategoryRequest;
import com.paathshala.dto.category.CategoryResponse;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import com.paathshala.exception.category.*;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.mapper.CategoryMapper;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CategoryRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;


    private final CategoryMapper categoryMapper;

    private final CourseMapper courseMapper;

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepo categoryRepo,CategoryMapper categoryMapper,CourseMapper courseMapper)
    {
        this.categoryRepo= categoryRepo;
        this.categoryMapper = categoryMapper;
        this.courseMapper = courseMapper;

    }

    public List<CategoryDetails> getAllCategory()
    {
        List<Category>  categories = categoryRepo.findAll();
        if(categories.isEmpty())
            throw new CategoryNotFoundException("Failed to retrieve categories : No category found"
            );
      return categoryMapper.toCategoryDetailsList(categories);
    }


    public List<CourseResponse> getCoursesByCategory(String categoryTitle)
    {
        Category category = categoryRepo.findByTitle(categoryTitle)
                .orElseThrow(
                        () -> new CategoryNotFoundException(String.format("Failed to retrieve courses : Category '%s' not found",categoryTitle))
                );

        List<Course> courses = category.getCourses();
        if(courses.isEmpty())
            throw new CourseNotFoundException(String.format("Failed to retrieve courses : Category '%s' has no courses",categoryTitle));
        else
            return courseMapper.toCourseResponseList(courses);
    }

    @Transactional
    public CategoryResponse addCategory(CategoryRequest request)
    {
        boolean isDuplicateExists = categoryRepo.existsByTitle(request.getTitle());
        if(isDuplicateExists)
            throw new CategoryDuplicateFoundException(String.format("Failed to add category : Category '%s' already exists",request.getTitle()));
        try {
            Category savedCategory = categoryRepo.save(categoryMapper.toEntity(request));
            ApiMessage message = new ApiMessage();
            message.setStatus("Category added");
            message.setDetails(String.format("Category '%s' created successfully",request.getTitle()));

            return categoryMapper.toCategoryResponse(savedCategory ,message);
        }
        catch(DataAccessException ex)
        {
           logger.error(ErrorType.CATEGORY_NOT_SAVED.toString(),ex);
           throw new CategorySaveFailedException(String.format("Failed to add category '%s' : Database Error",request.getTitle()));
        }
    }

    @Transactional
    public CategoryResponse updateCategory(CategoryRequest request, String categoryTitle)
    {
        if (request==null)
            throw new IllegalArgumentException("Category cannot be null");
        Category category = categoryRepo.findByTitle(categoryTitle)
                .orElseThrow(
                        () -> new CategoryNotFoundException(String.format("Failed to update category : Category '%s' not found",categoryTitle))
                );
        // set the fields
        Category modifiedCategory = categoryMapper.toEntity(request);
        modifiedCategory.setId(category.getId());
        modifiedCategory.setCourses(category.getCourses());
        modifiedCategory.setCreatedAt(category.getCreatedAt());


        if(!request.getTitle().equals(categoryTitle))
        {
            if(categoryRepo.existsByTitle(request.getTitle()))
                throw new CategoryDuplicateFoundException(
                        String.format("Failed to update category '%s' : Category '%s' already exists",categoryTitle,request.getTitle())
                );
        }
        try {
            Category updatedCategory = categoryRepo.save(modifiedCategory);
            ApiMessage message = new ApiMessage();
            message.setStatus("Category Updated");
            message.setDetails(String.format("Category '%s' updated to '%s' successfully",categoryTitle,request.getTitle()));
            return categoryMapper.toCategoryResponse(updatedCategory, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CATEGORY_NOT_UPDATED.toString(),ex);
            throw new CategoryUpdateFailedException(String.format("Failed to update category '%s' : Database Error",categoryTitle));
        }

    }

    @Transactional
    public CategoryResponse removeCategory(String categoryTitle)
    {
        if(categoryTitle.isEmpty())
            throw new IllegalArgumentException("Category cannot be null");
        Category category = categoryRepo.findByTitle(categoryTitle)
                .orElseThrow(
                        () -> new CategoryNotFoundException(String.format("Failed to delete category : Category '%s' not found",categoryTitle))
                );

        if(!category.getCourses().isEmpty())
            throw new CategoryDeleteFailedException(String.format("Failed to delete category : Category '%s' has courses",categoryTitle));
        try {
            categoryRepo.delete(category);
            ApiMessage message = new ApiMessage();
            message.setStatus("Category removed");
            message.setDetails(String.format("Category '%s' deleted successfully",categoryTitle));
            return categoryMapper.toCategoryResponse(category,message);
        }
        catch (DataAccessException ex)
        {
            logger.error(ErrorType.COURSE_NOT_DELETED.toString(),ex);
            throw new CategoryDeleteFailedException("Failed to delete category : Database error");
        }
    }

    public boolean isExists(String categoryTitle)
    {
        Optional<Category> savedCategory = categoryRepo.findByTitle(categoryTitle);
        return savedCategory.isPresent();

    }
}
