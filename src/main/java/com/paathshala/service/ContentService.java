package com.paathshala.service;

import com.paathshala.DTO.Content.ContentResponse;
import com.paathshala.DTO.Content.Note.NoteRequest;
import com.paathshala.DTO.Content.Note.NoteResponse;
import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import com.paathshala.mapper.ContentMapper;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.NoteRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContentService {

    private final CourseRepo courseRepo;
    private final NoteRepo noteRepo;
    private final ContentMapper contentMapper;

    public ContentService(NoteRepo noteRepo, CourseRepo courseRepo, ContentMapper contentMapper)
    {
        this.noteRepo=noteRepo;
        this.courseRepo=courseRepo;
        this.contentMapper=contentMapper;
    }



    @Transactional
    public NoteResponse addNote(NoteRequest noteRequest,String courseTitle)
    {
      Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        Map<String,Object> message = new HashMap<>();
        if(course.isEmpty())
        {
            message.put("status","No course "+courseTitle+" found");
            return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
        }
     Optional<List<Note>> notes = noteRepo.findByCourse(course.get());
        if(notes.isPresent())
        {
            for(Note note:notes.get())
            {
                if(note.getTitle().equals(noteRequest.getTitle()))
                {
                    message.put("status","Duplicate note title found in same course");
                    return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
                }
            }
        }
        Note note = contentMapper.toNoteEntity(noteRequest);
        note.setCourse(course.get());
        note = noteRepo.save(note);
        message.put("status","New note added to the course "+courseTitle);
        return contentMapper.toNoteResponseSuccess(note,false,message);
    }

    public NoteResponse getNoteByTitle(String noteTitle)
    {
        return new NoteResponse();
    }

}
