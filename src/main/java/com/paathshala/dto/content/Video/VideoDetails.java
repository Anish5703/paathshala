package com.paathshala.dto.content.Video;

import com.paathshala.dto.content.ContentDetails;

import java.time.LocalDateTime;


public class VideoDetails extends ContentDetails {

    private Integer contentLength; //in seconds

    public VideoDetails(int id, String title, int courseId, String description, String contentUrl, String contentType, Byte contentSize, LocalDateTime createdAt, Integer contentLength) {
        super(id, title, courseId, description, contentUrl, contentType, contentSize, createdAt);
        this.contentLength = contentLength;
    }

    public Integer getContentLength()
    {
        return contentLength;
    }
    public void setContentLength(Integer contentLength)
    {
        this.contentLength=contentLength;
    }
}
