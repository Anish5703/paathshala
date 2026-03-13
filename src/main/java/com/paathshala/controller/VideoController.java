package com.paathshala.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paathshala.dto.content.Video.VideoDetails;
import com.paathshala.dto.content.Video.VideoRequest;
import com.paathshala.dto.content.Video.VideoResponse;
import com.paathshala.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/course/{courseTitle}/video")
public class VideoController {

    private final VideoService videoService;
    private final ObjectMapper mapper;

    public VideoController(VideoService videoService)
    {
        this.videoService = videoService;
        this.mapper = new ObjectMapper();
    }

    @PostMapping(value ="/add" ,
    consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoResponse> createVideo(@Valid @RequestPart(value = "VideoRequest")String requestJson, @PathVariable String courseTitle,
                                                     MultipartFile video) throws JsonProcessingException
    {
        VideoRequest videoRequest = mapper.readValue(requestJson, VideoRequest.class);
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);

        VideoResponse response = videoService.addVideo(videoRequest,decodedCourseTitle,video);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);

    }

    @PutMapping(value="/{contentTitle}/update",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoResponse> modifyVideo(@PathVariable(name="courseTitle") String courseTitle,
                                                    @PathVariable(name="contentTitle") String contentTitle,
                                                    @Valid @RequestPart("VideoRequest") String requestJson,
                                                    @RequestPart(value = "file",required = false) MultipartFile video) throws JsonProcessingException
    {
        VideoRequest videoRequest = mapper.readValue(requestJson, VideoRequest.class);
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
        String decodedVideoTitle = URLDecoder.decode(contentTitle,StandardCharsets.UTF_8);

        VideoResponse response = videoService.updateVideo(videoRequest,decodedVideoTitle,decodedCourseTitle,video);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
    }

    @DeleteMapping("/{contentTitle}/delete")
    @PreAuthorize(("hasRole('ADMIN')"))
   public ResponseEntity<VideoResponse> deleteVideo(@PathVariable(name="courseTitle")String courseTitle,
@PathVariable(name="contentTitle")String contentTitle)
{
    String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
    String decodedVideoTitle = URLDecoder.decode(contentTitle,StandardCharsets.UTF_8);
    VideoResponse response = videoService.removeVideo(decodedCourseTitle,decodedVideoTitle);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
    return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
}
    @GetMapping("/{contentTitle}")
    public ResponseEntity<VideoResponse> getVideo(@PathVariable String contentTitle,
                                                @PathVariable String courseTitle)
    {
        String decodedContentTitle = URLDecoder.decode(contentTitle, StandardCharsets.UTF_8);
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
        VideoResponse response = videoService.getVideoByTitle(decodedContentTitle,decodedCourseTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);

    }
    @GetMapping("/all")
    public ResponseEntity<List<VideoDetails>> getVideos(@PathVariable String courseTitle)
    {
        String decodeCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
        List<VideoDetails> response = videoService.getVideoList(decodeCourseTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    }
}
