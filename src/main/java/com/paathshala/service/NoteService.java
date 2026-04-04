package com.paathshala.service;

import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.exception.content.*;
import com.paathshala.mapper.ContentMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.NoteRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class NoteService {

    private final CourseRepo courseRepo;
    private final NoteRepo noteRepo;
    private final ContentMapper contentMapper;
    private final ContentService contentService;

    @Value ("${note.dir}")
    private String noteDirectory;
    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    public NoteService(NoteRepo noteRepo, CourseRepo courseRepo, ContentMapper contentMapper,ContentService contentService)
    {
        this.noteRepo=noteRepo;
        this.courseRepo=courseRepo;
        this.contentMapper=contentMapper;
        this.contentService=contentService;
    }



    @Transactional
    public NoteResponse addNote(NoteRequest noteRequest,String courseTitle,MultipartFile file)  {
       /*
         Retrieve course object from database using title
         */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );

        /*
        Find notes linked to the course
        Search for note title duplication
         */

        boolean isNoteDuplicate = noteRepo.existsByTitleAndCourse(noteRequest.getTitle(),course);
        if(isNoteDuplicate)
        {
            String noteTitle = noteRequest.getTitle();
           throw new ContentDuplicateFoundException(String.format("Add Note '%s' failed : Note '%s' already exists on Course '%s'",noteTitle,noteTitle,courseTitle));
        }

        /*
         Map Request to Entity
         Save course to the entity
        */
        Note note = contentMapper.toNoteEntity(noteRequest);
        note.setCourse(course);

        /* Save file and assigned file properties */
         note= contentService.saveContentFileAndProperties(note,file,noteDirectory);

         /*
         Save note in the database and return success response
         else throw an exception
         */
        try {
            note = noteRepo.save(note);
           String message = "Note created successfully";
            return contentMapper.toNoteResponseSuccess(note, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_SAVED.toString(),ex.getMessage());
            throw new ContentSaveFailedException("Note Save Failed : Database Error");
        }
    }


    public List<NoteDetails> getNoteList(String courseTitle)
    {
        /*
         Retrieve course object from database using title
         */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
        );
        /*
          return the @NoteDetails List*/
        List<Note> notes = noteRepo.findByCourse(course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Course '%s' has no notes",courseTitle))
                );
        return contentMapper.toNoteDetailsList(notes);

    }
    @Transactional
    public NoteResponse getNoteByTitle(String noteTitle,String courseTitle)
    {
        /* Retrieve course object from database using title */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );

        /*
        Retrieve Note linked to the course
        throw an exception if not found any
        else return success response
         */

        Note note = noteRepo.findByTitleAndCourse(noteTitle,course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Note '%s' not found om Course '%s'",noteTitle,courseTitle))
                );
           String message = "Note found";
            return contentMapper.toNoteResponseSuccess(note,message);

    }

    @Transactional
    public NoteResponse updateNote(NoteRequest noteRequest, String noteTitle, String courseTitle, MultipartFile file)

    {
        /*
        Retrieve Course object from database using course title
        throw exception if not found
        */
        Course course =  courseRepo.findByTitle(courseTitle).orElseThrow(
                () -> new CourseNotFoundException(String.format("Update Note '%s' failed : Course '%s' not found",noteTitle,courseTitle))
        );

        /*
         Retrieve Note object using note title
         return error response if not found
         */
        Note note = noteRepo.findByTitleAndCourse(noteTitle,course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Update Note '%s' failed : Note '%s' not found on Course '%s'",noteTitle,noteTitle,courseTitle ))
                );
        /*
        check new note title for duplication
        throw exception  if found
        */
        if(!note.getTitle().equals(noteTitle))
        {
            boolean duplicateNoteExists = noteRepo.existsByTitleAndCourse(noteTitle,course);
            if(duplicateNoteExists)
                throw new ContentDuplicateFoundException(String.format("Update Note '%s' failed : Note '%s' already exists on Course '%s",noteTitle,noteTitle,courseTitle));
        }
        /*
         Map Note dto to entity
         assign course to note
         set original note id
         */
        Note modifiedNote = contentMapper.toNoteEntity(noteRequest);
        modifiedNote.setCourse(course);
        modifiedNote.setId(note.getId());

        /*
        check if the content is updated
        if not skip the file saving process
        */
        if(file != null) {
            boolean isHashEqual = contentService.isHashEqual(note.getContentHash(), file);
            if (!isHashEqual) {
                modifiedNote = contentService.saveContentFileAndProperties(modifiedNote, file, noteDirectory);
            }
            else
            {
                modifiedNote = note;
                modifiedNote.setDescription(noteRequest.getDescription());
            }
        }
        else{
            modifiedNote = note;
            modifiedNote.setDescription(noteRequest.getDescription());
        }
        try {
            Note updatedNote = noteRepo.save(modifiedNote);
            String message = ("Note updated successfully");
            return contentMapper.toNoteResponseSuccess(updatedNote, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_UPDATED.toString(),ex.getMessage());
            throw new ContentUpdateFailedException(String.format("Update Note '%s' Failed : Database Error",noteTitle));
        }

    }

    @Transactional
    public NoteResponse removeNote(String courseTitle,String noteTitle) throws ContentDeleteFailedException
    {
            /* Retrieve Course from title
            check if course exists
            not found throw an exception
             */
            Course course = courseRepo.findByTitle(courseTitle)
                    .orElseThrow(
                            () -> new CourseNotFoundException(String.format("Course '%s' not found : Delete note '%s' failed",courseTitle,noteTitle))
                    );

            /* Retrieve Note from title and course
            if not found throw an exception
            if found delete the note from repository
             */
            Note note = noteRepo.findByTitleAndCourse(noteTitle, course)
                    .orElseThrow(
                            () -> new ContentNotFoundException(String.format("Note '%s' not found on course '%s'",noteTitle,courseTitle))
                    );
            try{
            noteRepo.deleteById(note.getId());
            String message = "Note removed successfully";
            return contentMapper.toNoteResponseSuccess(note,message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_DELETED.toString(),ex);
            throw new ContentDeleteFailedException(String.format("DataBase error : Failed to delete note '%s'",note.getTitle()),ex.getCause());
        }

    }




}
