package com.paathshala.controller;

import com.paathshala.DTO.Content.Note.NoteDetails;
import com.paathshala.DTO.Content.Note.NoteRequest;
import com.paathshala.DTO.Content.Note.NoteResponse;
import com.paathshala.DTO.Course.CourseResponse;
import com.paathshala.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("api/{courseTitle}/note")
public class NoteController {

    private final ContentService contentService;

    public NoteController(ContentService contentService)
    {
        this.contentService=contentService;
    }

@PostMapping("/add")
@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request,
                                                   @PathVariable String courseTitle,
                                                   MultipartFile file) throws IOException
{
    String decodedCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
    NoteResponse response = contentService.addNote(request,courseTitle,file);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
    if(!response.isError())
        return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);
    else
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(header).body(response);
}
@GetMapping("/{contentTitle}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable String contentTitle,
                                                @PathVariable String courseTitle)
{
    String decodedContentTitle = URLDecoder.decode(contentTitle, StandardCharsets.UTF_8);
    String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
    NoteResponse response = contentService.getNoteByTitle(decodedContentTitle,decodedCourseTitle);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
    if(!response.isError())
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);
    else
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).headers(header).body(response);
}
@GetMapping("/all")
    public ResponseEntity<List<NoteDetails>> getNotes(@PathVariable String courseTitle)
{
    String decodeCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
    List<NoteDetails> notes = contentService.getNoteList(decodeCourseTitle);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
    if(!notes.isEmpty())
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(notes);
    else
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).headers(header).body(notes);
}
}
