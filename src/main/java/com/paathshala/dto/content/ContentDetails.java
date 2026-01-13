package com.paathshala.dto.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDetails {

    private Integer id;
    private String title;
    private String courseTitle;
    private String description;
    private String contentUrl;
    private String contentType;
    private Long contentSize;
    private LocalDateTime createdAt;

}
