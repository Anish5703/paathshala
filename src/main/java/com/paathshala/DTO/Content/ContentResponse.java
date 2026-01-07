package com.paathshala.DTO.Content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentResponse {
    private int id;
    private String title;
    private int courseId;
    private String description;
    private String contentUrl;
    private String contentType;
    private Byte contentSize;
    private LocalDateTime createdAt;

    private Map<String,Object> message;
    private boolean isError;
}
