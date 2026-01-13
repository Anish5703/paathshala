package com.paathshala.mapper;

import com.paathshala.dto.ApiMessage;
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



    public Note toNoteEntity(NoteRequest request)
    {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        return note;
    }

    public NoteResponse toNoteResponseError(String noteTitle, ApiMessage message)
    {
        NoteResponse noteResponse = new NoteResponse();
        noteResponse.setTitle(noteTitle);
        noteResponse.setMessage(message);
        return noteResponse;
    }
    public NoteResponse toNoteResponseSuccess(Note note,ApiMessage message)
    {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setDescription(note.getDescription());
        response.setCourseTitle(note.getCourse().getTitle());
        response.setContentUrl(note.getContentUrl());
        response.setContentType(note.getContentType());
        response.setContentSize(note.getContentSize());
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
