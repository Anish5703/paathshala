package com.paathshala.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {


    private int id;
    private String title;
    private String description;
    private Map<String,Object> message;
    private boolean isError;

    public CategoryResponse(Map<String,Object> message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }
}
