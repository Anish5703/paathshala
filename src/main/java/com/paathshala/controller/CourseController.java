package com.paathshala.controller;

import com.paathshala.DTO.Course.CourseDetails;
import com.paathshala.DTO.Course.CourseRequest;
import com.paathshala.DTO.Course.CourseResponse;
import com.paathshala.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
      if(!response.isError())
          return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
      else
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);
    }

    @PutMapping("/edit/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> modifyCourse(@Valid @RequestBody CourseRequest request,@PathVariable int courseId)
    {
        CourseResponse response = courseService.editCourse(request,courseId);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isError())
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(response);
        else
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);

    }

    @DeleteMapping("/remove/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> deleteCourse(@PathVariable int courseId)
    {
        CourseResponse response = courseService.removeCourse(courseId);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        if(!response.isError())
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(response);
        else
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);
    }
}
