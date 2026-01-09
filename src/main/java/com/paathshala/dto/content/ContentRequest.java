package com.paathshala.dto.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentRequest {

    private String title;
    private int courseId;
    private String description;
    private String contentType;
    private Long contentSize;

    public ContentRequest(String title, int courseId, String description)
    {
        this.title=title;
        this.courseId=courseId;
        this.description=description;
    }

}
