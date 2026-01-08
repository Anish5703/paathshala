package com.paathshala.DTO.Content.Video;

import com.paathshala.DTO.Content.ContentRegister;

public class VideoRequest extends ContentRegister {

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
