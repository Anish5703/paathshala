package com.paathshala.controller;

import com.paathshala.dto.enrollment.EnrollmentRequest;import com.paathshala.dto.enrollment.EnrollmentResponse;
import com.paathshala.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;


    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enroll(
            @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enrollFree(request));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<EnrollmentResponse>> getUserEnrollments(
            @PathVariable String username) {
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(username));
    }
}