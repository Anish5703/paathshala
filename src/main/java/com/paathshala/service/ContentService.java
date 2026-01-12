package com.paathshala.service;

import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.entity.Content;
import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import com.paathshala.exception.*;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.exception.note.*;
import com.paathshala.mapper.ContentMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.NoteRepo;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class ContentService {

    private final CourseRepo courseRepo;
    private final NoteRepo noteRepo;
    private final ContentMapper contentMapper;

    @Value ("${note.dir}")
    private String noteDirectory;
    private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

    public ContentService(NoteRepo noteRepo, CourseRepo courseRepo, ContentMapper contentMapper)
    {
        this.noteRepo=noteRepo;
        this.courseRepo=courseRepo;
        this.contentMapper=contentMapper;
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
        Map<String,Object> message = new HashMap<>();

        /*
        Find notes linked to the course
        Search for note title duplication
         */

        boolean isNoteDuplicate = noteRepo.existsByTitleAndCourse(noteRequest.getTitle(),course);
        if(isNoteDuplicate)
        {
            String noteTitle = noteRequest.getTitle();
           throw new NoteDuplicateFoundException(String.format("Add Note '%s' failed : Note '%s' already exists on Course '%s'",noteTitle,noteTitle,courseTitle));
        }

        /*
         Map Request to Entity
         Save course to the entity
        */
        Note note = contentMapper.toNoteEntity(noteRequest);
        note.setCourse(course);

        /* Save file and assigned file properties */
         note= saveContentFileAndProperties(note,file,noteDirectory);

         /*
         Save note in the database and return success response
         else throw an exception
         */
        try {
            note = noteRepo.save(note);
            message.put("status", "Note added");
            message.put("details", "New note " + note.getTitle() + " added to the course " + courseTitle);
            return contentMapper.toNoteResponseSuccess(note, false, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.NOTE_NOT_SAVED.toString(),ex);
            throw new NoteSaveFailedException("Note Save Failed : Database Error");
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
                        () -> new NoteNotFoundException(String.format("Course '%s' has no notes",courseTitle))
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
        Map<String,Object> message = new HashMap<>();

        /*
        Retrieve Note linked to the course
        throw an exception if not found any
        else return success response
         */

        Note note = noteRepo.findByTitleAndCourse(noteTitle,course)
                .orElseThrow(
                        () -> new NoteNotFoundException(String.format("Note '%s' not found om Course '%s'",noteTitle,courseTitle))
                );

            message.put("status","Note found");
            return contentMapper.toNoteResponseSuccess(note,false,message);

    }

    @Transactional
    public NoteResponse updateNote(NoteRequest noteRequest, String noteTitle, String courseTitle, MultipartFile file)

    {
        /*
        Retrieve Course object from database using coursetitle
        throw exception if not found
        */
        Course course =  courseRepo.findByTitle(courseTitle).orElseThrow(
                () -> new CourseNotFoundException(String.format("Update Note '%s' failed : Course '%s' not found",noteTitle,courseTitle))
        );;
        Map<String,Object> message = new HashMap<>();

        /*
         Retrieve Note object using note title
         return error response if not found
         */
        Note note = noteRepo.findByTitleAndCourse(noteTitle,course)
                .orElseThrow(
                        () -> new NoteNotFoundException(String.format("Update Note '%s' failed : Note '%s' not found on Course '%s'",noteTitle,noteTitle,courseTitle ))
                );
        /*
        check new note title for duplication
        throw exception  if found
        */
        if(!note.getTitle().equals(noteTitle))
        {
            boolean duplicateNoteExists = noteRepo.existsByTitleAndCourse(noteTitle,course);
            if(duplicateNoteExists)
                throw new NoteDuplicateFoundException(String.format("Update Note '%s' failed : Note '%s' already exists on Course '%s",noteTitle,noteTitle,courseTitle));
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
        boolean isHashEqual = isHashEqual(note.getContentHash(),file);
        if(!isHashEqual )
        {
            modifiedNote = saveContentFileAndProperties(modifiedNote,file,noteDirectory);
        }
        try {
            Note updatedNote = noteRepo.save(modifiedNote);
            message.put("status", "Note updated");
            return contentMapper.toNoteResponseSuccess(updatedNote, false, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.NOTE_NOT_UPDATED.toString(),ex);
            throw new NoteUpdateFailedException(String.format("Update Note '%s' Failed : Database Error",noteTitle));
        }

    }

    @Transactional
    public NoteResponse removeNote(String courseTitle,String noteTitle) throws NoteDeleteFailedException
    {
            /* Retrieve Course from title
            check if course exists
            if not found throw an exception
             */
            Course course = courseRepo.findByTitle(courseTitle)
                    .orElseThrow(
                            () -> new CourseNotFoundException(String.format("Course '%s' not found : Delete note '%s' failed",courseTitle,noteTitle))
                    );

            Map<String, Object> message = new HashMap<>();
            /* Retrieve Note from title and course
            if not found throw an exception
            if found delete the note from repository
             */
            Note note = noteRepo.findByTitleAndCourse(noteTitle, course)
                    .orElseThrow(
                            () -> new NoteNotFoundException(String.format("Note '%s' not found on course '%s'",noteTitle,courseTitle))
                    );
            try{
            noteRepo.deleteById(note.getId());
            message.put("status", "Note deleted");
            return contentMapper.toNoteResponseSuccess(note, false, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.NOTE_NOT_DELETED.toString(),ex);
            throw new NoteDeleteFailedException(String.format("DataBase error : Failed to delete note '%s'",note.getTitle()),ex.getCause());
        }

    }


       /*
        Generate unique file url
        save to the directory
        save content properties i.e. url,type,size
        returns null object if operation fails and content object if succeeds
         */
        @Transactional
        public <T extends Content> T saveContentFileAndProperties(
            T content,
            MultipartFile file,
            String uploadDirectory) {

        String uniqueFileName;
        String newHash;

        try {
            newHash = calculateHash(file);

            // Same file uploaded
            if (newHash.equals(content.getContentHash())) {
                logger.info("Same file detected, skipping save");
                return content;
            }

            uniqueFileName = storeFile(file, uploadDirectory, content.getContentUrl());

        } catch (IOException e) {
            logger.error(ErrorType.FILE_UPLOAD_FAILED.toString(), e);
            throw new FileUploadFailedException("File upload failed");
        }

        // DB-related changes (transaction-safe)
        content.setContentUrl(uniqueFileName);
        content.setContentType(file.getContentType());
        content.setContentSize(file.getSize());
        content.setContentHash(newHash);

        return content;
    }




    private String storeFile(
            MultipartFile file,
            String uploadDirectory,
            String oldFileName) throws IOException {

        Path directoryPath = Paths.get(uploadDirectory);
        Files.createDirectories(directoryPath);

        if (oldFileName != null) {
            Files.deleteIfExists(directoryPath.resolve(oldFileName));
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID() + "." + extension;

        Files.write(
                directoryPath.resolve(uniqueFileName),
                file.getBytes()
        );

        return uniqueFileName;
    }

    private  boolean isHashEqual(String contentHash,MultipartFile file){

            String newHash = calculateHash(file);
            return contentHash != null &&
                    contentHash.equals(newHash);
        }


    private String calculateHash(MultipartFile file)  {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        }
        catch(NoSuchAlgorithmException | IOException ex)
        {
            logger.error("File hash calculation error : {}",ex.getMessage());
            throw new FileUploadFailedException("File hash calculation error");
        }
    }


}
