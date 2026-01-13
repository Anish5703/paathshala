package com.paathshala.controller;

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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/all")
    public ResponseEntity<List<CourseDetails>> allCourses()
    {
        List<CourseDetails> response = courseService.getAllCourse();
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(response.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(header).body(response);
        else
            return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request)
    {
      CourseResponse response = courseService.addCourse(request);
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
