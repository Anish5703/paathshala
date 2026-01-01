package com.paathshala.mapper;

import com.paathshala.DTO.Category.CategoryRequest;
import com.paathshala.DTO.Category.CategoryResponse;
import com.paathshala.entity.Category;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category,boolean error , Map<String,Object> message)

    {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getTitle());
        response.setDescription(category.getDescription());
        response.setError(error);
        response.setMessage(message);
        return response;
    }

    public Category toEntity(CategoryRequest request)
    {
        Category category = new Category(request.getTitle(), request.getDescription());
        if(request.getId()>0)
            category.setId(request.getId());
        return category;
    }
}
