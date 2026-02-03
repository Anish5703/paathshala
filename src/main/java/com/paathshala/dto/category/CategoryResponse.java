package com.paathshala.dto.category;

import com.paathshala.dto.ApiMessage;
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
    private ApiMessage message;
    public CategoryResponse(ApiMessage message) {
       
    }
}
