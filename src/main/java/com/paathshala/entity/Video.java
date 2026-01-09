package com.paathshala.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="video_tbl")
@NoArgsConstructor
@Getter
@Setter
public class Video extends Content{
    private Integer contentLength; //measure in seconds

    public Video(String title, Course course, String description, String contentUrl, String contentType, Long contentSize, Integer contentLength)
    {
        super(title,course,description,contentUrl,contentType,contentSize);
        this.contentLength=contentLength;
    }
}
