package com.paathshala.controller;

import com.paathshala.dto.mcq.McqQuestionRequest;
import com.paathshala.dto.mcq.McqQuestionResponse;
import com.paathshala.dto.mcq.McqResultResponse;
import com.paathshala.dto.mcq.McqSubmitRequest;
import com.paathshala.service.McqService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course/{courseTitle}/mcq")
public class McqController {

    private final McqService mcqService;

    public McqController(McqService mcqService)
    {
        this.mcqService = mcqService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<McqQuestionResponse>> getQuestions(
            @PathVariable String courseTitle,
            @RequestParam(defaultValue = "EASY") String difficulty,
            @RequestParam(defaultValue = "10") int limit) {
        List<McqQuestionResponse> questions = mcqService.getQuestions(courseTitle, difficulty, limit);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<McqResultResponse> submitAnswers(
            @PathVariable String courseTitle,
            @RequestBody @Valid McqSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        McqResultResponse result = mcqService.submitAnswers(
                courseTitle, userDetails.getUsername(), request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<McqQuestionResponse>> getAllQuestions(
            @PathVariable String courseTitle) {
        List<McqQuestionResponse> questions = mcqService.getAllQuestions(courseTitle);
        return ResponseEntity.ok(questions);
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<McqQuestionResponse> addQuestion(
            @PathVariable String courseTitle,
            @RequestBody @Valid McqQuestionRequest request) {
        McqQuestionResponse created = mcqService.addQuestion(courseTitle, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{questionId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<McqQuestionResponse> updateQuestion(
            @PathVariable String courseTitle,
            @PathVariable Integer questionId,
            @RequestBody @Valid McqQuestionRequest request) {
        McqQuestionResponse updated = mcqService.updateQuestion(courseTitle, questionId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{questionId}/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable String courseTitle,
            @PathVariable Integer questionId) {
        mcqService.deleteQuestion(courseTitle, questionId);
        return ResponseEntity.noContent().build();
    }

}
