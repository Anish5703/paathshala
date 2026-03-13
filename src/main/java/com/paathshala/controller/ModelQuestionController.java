package com.paathshala.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionDetails;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionRequest;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionResponse;
import com.paathshala.dto.content.Video.VideoDetails;
import com.paathshala.dto.content.Video.VideoRequest;
import com.paathshala.dto.content.Video.VideoResponse;
import com.paathshala.service.ModelQuestionService;
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
@RequestMapping("/api/course/{courseTitle}/model-question")
public class ModelQuestionController {

    private final ModelQuestionService modelQuestionService;
    private final ObjectMapper mapper;

    public ModelQuestionController(ModelQuestionService modelQuestionService)
    {
        this.modelQuestionService = modelQuestionService;
        this.mapper = new ObjectMapper();
    }

    @PostMapping(value ="/add" ,
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModelQuestionResponse> createModelQuestion(@Valid @RequestPart(value = "ModelQuestionRequest")String requestJson, @PathVariable String courseTitle,
                                                                     @RequestPart(value="file",required = true) MultipartFile modelQuestion) throws JsonProcessingException
    {
        ModelQuestionRequest modelQuestionRequest = mapper.readValue(requestJson, ModelQuestionRequest.class);
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);

        ModelQuestionResponse response = modelQuestionService.addModelQuestion(modelQuestionRequest,decodedCourseTitle,modelQuestion);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);

    }

    @PutMapping(value="/{contentTitle}/update",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModelQuestionResponse> modifyModelQuestion(@PathVariable(name="courseTitle") String courseTitle,
                                                     @PathVariable(name="contentTitle") String contentTitle,
                                                     @Valid @RequestPart("VideoRequest") String requestJson,
                                                     @RequestPart(value = "file",required = false) MultipartFile modelQuestion) throws JsonProcessingException
    {
        ModelQuestionRequest modelQuestionRequest = mapper.readValue(requestJson, ModelQuestionRequest.class);
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
        String decodedModelQuestionTitle = URLDecoder.decode(contentTitle,StandardCharsets.UTF_8);

        ModelQuestionResponse response = modelQuestionService.updateModelQuestion(modelQuestionRequest,decodedModelQuestionTitle,decodedCourseTitle,modelQuestion);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
    }

    @DeleteMapping("/{contentTitle}/delete")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<ModelQuestionResponse> deleteModelQuestion(@PathVariable(name="courseTitle")String courseTitle,
                                                     @PathVariable(name="contentTitle")String contentTitle)
    {
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
        String decodedModelQuestionTitle = URLDecoder.decode(contentTitle,StandardCharsets.UTF_8);
        ModelQuestionResponse response = modelQuestionService.removeModelQuestion(decodedCourseTitle,decodedModelQuestionTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
    }
    @GetMapping("/{contentTitle}")
    public ResponseEntity<ModelQuestionResponse> getModelQuestion(@PathVariable String contentTitle,
                                                  @PathVariable String courseTitle)
    {
        String decodedContentTitle = URLDecoder.decode(contentTitle, StandardCharsets.UTF_8);
        String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
        ModelQuestionResponse response = modelQuestionService.getModelQuestionByTitle(decodedContentTitle,decodedCourseTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);

    }
    @GetMapping("/all")
    public ResponseEntity<List<ModelQuestionDetails>> getModelQuestions(@PathVariable String courseTitle)
    {
        String decodeCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
        List<ModelQuestionDetails> response = modelQuestionService.getModelQuestionList(decodeCourseTitle);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    }
}
