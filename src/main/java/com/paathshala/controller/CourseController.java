package com.paathshala.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.course.CourseDetails;
import com.paathshala.dto.course.CourseRequest;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseTitle}")
    public ResponseEntity<CourseDetails> getCourse(@PathVariable String courseTitle)
    {
        String decodedTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
        CourseDetails response = courseService.getCourse(decodedTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);


    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDetails>> allCourses()
    {
        List<CourseDetails> response = courseService.getAllCourse();
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    }

    @PostMapping(value="/add" ,
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestPart("CourseRequest") String requestJson,
                                                        @RequestPart("image") MultipartFile image) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        CourseRequest request = mapper.readValue(requestJson, CourseRequest.class);

      CourseResponse response = courseService.addCourse(request,image);
      HttpHeaders header = new HttpHeaders();
      header.set("Content-Type","application/json");
      return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
    }

    @PutMapping("/{courseTitle}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> modifyCourse(@Valid @RequestBody CourseRequest request,@PathVariable String courseTitle)
    {
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
        CourseResponse response = courseService.updateCourse(request,decodedCourseTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);


    }

    @DeleteMapping("/{courseTitle}/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> deleteCourse(@PathVariable String courseTitle)
    {
        String decodedCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
        CourseResponse response = courseService.removeCourse(decodedCourseTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
            return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    }
}
