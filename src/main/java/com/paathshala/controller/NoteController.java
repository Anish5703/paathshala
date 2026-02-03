package com.paathshala.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.service.ContentService;
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
@RequestMapping("api/course/{courseTitle}/note")
public class NoteController {

    private final ContentService contentService;

    public NoteController(ContentService contentService)
    {
        this.contentService=contentService;
    }

@PostMapping(value="/add",
        consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestPart("request") String requestJson,
                                                   @PathVariable String courseTitle,
                                                   @RequestPart("file") MultipartFile file) throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    NoteRequest request = mapper.readValue(requestJson, NoteRequest.class);

    String decodedCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
    NoteResponse response = contentService.addNote(request,decodedCourseTitle,file);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
    return ResponseEntity.status(HttpStatus.CREATED).headers(header).body(response);

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
    return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);

}
@GetMapping("/all")
    public ResponseEntity<List<NoteDetails>> getNotes(@PathVariable String courseTitle)
{
    String decodeCourseTitle = URLDecoder.decode(courseTitle,StandardCharsets.UTF_8);
    List<NoteDetails> notes = contentService.getNoteList(decodeCourseTitle);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(notes);
}
@PreAuthorize("hasRole('ADMIN')")
@PutMapping(value="/{contentTitle}/update",
        consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteResponse> modifyNote(@PathVariable String contentTitle,
                                                   @PathVariable String courseTitle,
                                                   @Valid @RequestPart("request") String requestJson,
                                                   @RequestPart(value = "file",required = false) MultipartFile file) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    NoteRequest noteRequest = mapper.readValue(requestJson, NoteRequest.class);

    String decodedContentTitle = URLDecoder.decode(contentTitle, StandardCharsets.UTF_8);
    String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
    NoteResponse response = contentService.updateNote(noteRequest,decodedContentTitle,decodedCourseTitle,file);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(header).body(response);

}
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{contentTitle}/remove")
    public ResponseEntity<NoteResponse> deleteNote(@PathVariable String contentTitle,
                                                   @PathVariable String courseTitle)
{
    String decodedContentTitle = URLDecoder.decode(contentTitle, StandardCharsets.UTF_8);
    String decodedCourseTitle = URLDecoder.decode(courseTitle, StandardCharsets.UTF_8);
    NoteResponse response = contentService.removeNote(decodedCourseTitle,decodedContentTitle);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type","application/json");
        return ResponseEntity.status(HttpStatus.ACCEPTED).headers(header).body(response);


}

}
