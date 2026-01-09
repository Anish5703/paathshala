package com.paathshala.DTO.Content;

import com.paathshala.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentRegister {

    private String title;
    private int courseId;
    private String description;
    private String contentType;
    private Long contentSize;

    public ContentRegister(String title,int courseId,String description)
    {
        this.title=title;
        this.courseId=courseId;
        this.description=description;
    }

}
