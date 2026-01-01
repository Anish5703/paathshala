package com.paathshala.DTO.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategoryRequest {

    private int id;
    @NotBlank(message = "Category title is required")
    private String title;

    private String description;

    public CategoryRequest(String title , String description)
    {
        this.title = title;
        this.description = description;
    }
}