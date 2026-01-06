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

    @Column(nullable = false)
    private String contentUrl;
    private String contentType;
    private Byte contentSize;
    private Integer contentLength; //measure in seconds

    public Video(String title, Course course, String description, String contentUrl, String contentType, Byte contentSize, Integer contentLength)
    {
        super(title,course,description);
        this.contentUrl=contentUrl;
        this.contentType=contentType;
        this.contentSize=contentSize;
    }
}
