package com.paathshala.dto.content;

import com.paathshala.dto.course.CourseDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponse {
    private Integer id;
    private String title;
    private CourseDetails course;
    private String description;
    private String contentUrl;
    private String contentType;
    private Long contentSize;
    private LocalDateTime createdAt;

    private Map<String,Object> message;
    private boolean isError;

    public ContentResponse(String title,boolean isError,Map<String,Object> message)
    {
        this.title=title;
        this.message=message;
        this.isError=isError;
    }
}
