package com.paathshala.DTO.Content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDetails {

    private int id;
    private String title;
    private int courseId;
    private String description;
    private String contentUrl;
    private String contentType;
    private Long contentSize;
    private LocalDateTime createdAt;

}
