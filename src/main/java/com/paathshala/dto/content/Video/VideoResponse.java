package com.paathshala.dto.content.Video;

import com.paathshala.dto.content.ContentDetails;

import java.time.LocalDateTime;

public class VideoResponse extends ContentDetails {

    private Integer contentLength;

    public VideoResponse(int id, String title, String courseTitle, String description, String contentUrl, String contentType, Long contentSize, LocalDateTime createdAt, Integer contentLength) {
        super(id, title, courseTitle, description, contentUrl, contentType, contentSize, createdAt);
        this.contentLength = contentLength;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }
}
