package com.paathshala.mapper;

import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.entity.Note;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    public NoteDetails toNoteDetails(Note note)
    {
        NoteDetails noteDetails = new NoteDetails();
        noteDetails.setId(note.getId());
        noteDetails.setTitle(note.getTitle());
        noteDetails.setContentUrl(note.getContentUrl());
        noteDetails.setContentType(note.getContentType());
        noteDetails.setContentSize(note.getContentSize());
        return noteDetails;
    }

    public List<NoteDetails> toNoteDetailsList(List<Note> notes)
    {
        if(notes.isEmpty()) return null;
        List<NoteDetails> noteDetailsList = new ArrayList<>();
        for(Note note : notes )
        {
            noteDetailsList.add(toNoteDetails(note));
        }
        return noteDetailsList;
    }
}
