package com.paathshala.DTO.Content.Video;

import com.paathshala.DTO.Content.ContentRequest;

public class VideoRequest extends ContentRequest {

    private Integer contentLength;

    public VideoRequest(String title, int courseId, String description, String contentUrl, String contentType, Byte contentSize, Integer contentLength) {
        super(title, courseId, description, contentUrl, contentType, contentSize);
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
