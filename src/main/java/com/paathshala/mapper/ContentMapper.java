package com.paathshala.mapper;

import com.paathshala.DTO.Content.Note.NoteRequest;
import com.paathshala.DTO.Content.Note.NoteResponse;
import com.paathshala.entity.Note;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ContentMapper {

    private final CourseMapper courseMapper;

    public ContentMapper(CourseMapper courseMapper)
    {
        this.courseMapper=courseMapper;
    }


    public Note toNoteEntity(NoteRequest request)
    {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        note.setContentUrl(request.getContentUrl());
        note.setContentType(request.getContentType());
        note.setContentSize(request.getContentSize());
        return note;
    }

    public NoteResponse toNoteResponseError(String noteTitle, boolean isError, Map<String,Object> message)
    {
        return new NoteResponse(
                noteTitle, true, message
        );
    }
    public NoteResponse toNoteResponseSuccess(Note note,boolean isError,Map<String,Object> message)
    {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setDescription(note.getDescription());
        response.setCourse(courseMapper.toCourseDetails(note.getCourse()));
        response.setContentUrl(note.getContentUrl());
        response.setContentType(note.getContentType());
        response.setContentSize(note.getContentSize());
        response.setError(isError);
        response.setMessage(message);
        return response;

    }
}
