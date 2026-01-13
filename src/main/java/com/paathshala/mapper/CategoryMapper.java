package com.paathshala.mapper;

import com.paathshala.dto.ApiMessage;
import com.paathshala.dto.category.CategoryDetails;
import com.paathshala.dto.category.CategoryRequest;
import com.paathshala.dto.category.CategoryResponse;
import com.paathshala.entity.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category, ApiMessage message)

    {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getTitle());
        response.setDescription(category.getDescription());
        response.setMessage(message);
        return response;
    }
    public CategoryResponse toCategoryResponse(Category category)

    {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setTitle(category.getTitle());
        response.setDescription(category.getDescription());
        return response;
    }
    public CategoryDetails toCategoryDetails(Category category)
    {
        CategoryDetails details = new CategoryDetails();
        details.setTitle(category.getTitle());
        details.setDescription(category.getDescription());
        return details;
    }

  public List<CategoryDetails> toCategoryDetailsList(List<Category> categories)
  {
      List<CategoryDetails> details = new ArrayList<>();
      for(Category category : categories)
      {
          details.add(toCategoryDetails(category));
      }
      return details;
  }

    public Category toEntity(CategoryRequest request)
    {
       return new Category(request.getTitle(), request.getDescription());

    }
}
