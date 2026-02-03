package com.paathshala.dto.content;

import com.paathshala.dto.ApiMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponse {
    private Integer id;
    private String title;
    private String courseTitle;
    private String description;
    private String contentUrl;
    private String contentType;
    private Long contentSize;
    private LocalDateTime createdAt;

private ApiMessage message;

    public ContentResponse(String title,ApiMessage message)
    {
        this.title=title;
        this.message=message;
    }
}
