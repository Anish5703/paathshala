package com.paathshala.controller;

import com.paathshala.dto.StudentDetails;
import com.paathshala.service.AdminDashboardService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService)
    {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/number-of-Students")
    public ResponseEntity<Long> getTotalNumberOfStudents()
    {
      return ResponseEntity.ok(adminDashboardService.getTotalNumberOfStudent());
    }

    @GetMapping("/number-of-EnrolledStudents")
    public ResponseEntity<Long> getTotalNumberOfEnrolledStudents()
    {
        return ResponseEntity.ok(adminDashboardService.getTotalNumberOfEnrolledStudent());
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentDetails>> getAllStudentDetails()
    {
        return ResponseEntity.ok(adminDashboardService.getAllStudents());
    }
}
