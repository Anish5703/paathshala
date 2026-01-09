package com.paathshala.service;

import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.entity.Content;
import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import com.paathshala.exception.CourseNotFoundException;
import com.paathshala.exception.FileUploadFailedException;
import com.paathshala.exception.NoteDeletionFailedException;
import com.paathshala.exception.NoteNotFoundException;
import com.paathshala.mapper.ContentMapper;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
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
        /* Find course object from database using title */
      Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        Map<String,Object> message = new HashMap<>();
        /* Return error if course is not found*/
        if(course.isEmpty())
        {
            message.put("status","Course not found");
            message.put("details","No course "+courseTitle+" found");
            return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
        }
        /*
        Find notes linked to the course
        Search for note title duplication
         */

        boolean isNoteDuplicate = noteRepo.existsByTitleAndCourse(noteRequest.getTitle(),course.get());
        if(isNoteDuplicate)
        {
            message.put("status","Note title duplication ");
            message.put("details","Duplicate note title found in course "+courseTitle);
            return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
        }

        /*
         Map Request to Entity
         Save course to the entity
        */
        Note note = contentMapper.toNoteEntity(noteRequest);
        note.setCourse(course.get());

        /* Save file and assigned file properties */
         note= saveContentFileAndProperties(note,file,noteDirectory);

         if(note==null)
         {
             message.put("status","File upload error");
             return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
         }
         /* Save note int the database and return success response */
        note = noteRepo.save(note);
        message.put("status","Note added");
        message.put("details","New note "+note.getTitle()+" added to the course "+courseTitle);
        return contentMapper.toNoteResponseSuccess(note,false,message);
    }


    public List<NoteDetails> getNoteList(String courseTitle)
    {
        /* Find course object from database using title */
        Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        /*
         Return null if course is not found in the database
         else return the @NoteDetails List*/
        if(course.isEmpty()) return Collections.emptyList();
        Optional<List<Note>> notes = noteRepo.findByCourse(course.get());
        if(notes.isEmpty()) return Collections.emptyList();

        return contentMapper.toNoteDetailsList(notes.get());

    }
    @Transactional
    public NoteResponse getNoteByTitle(String noteTitle,String courseTitle)
    {
        /* Retrieve course object from database using title */
        Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        Map<String,Object> message = new HashMap<>();
        /* Return error if course is not found*/
        if(course.isEmpty())
        {
            message.put("status","Course not found");
            message.put("details","No course "+courseTitle+" found");
            return contentMapper.toNoteResponseError(noteTitle,true,message);
        }
        /*
        Retrieve Note List linked to the course
        Search for matching note title
        return success response if found any
         */

        Optional<Note> note = noteRepo.findByTitleAndCourse(noteTitle,course.get());
        if(note.isPresent())
        {
            message.put("status","Note found");
            return contentMapper.toNoteResponseSuccess(note.get(),false,message);
        }

        /* return error response*/
        message.put("status","Note not found");
        message.put("details","Unable to locate note "+noteTitle+" in course "+courseTitle);
        return contentMapper.toNoteResponseError(noteTitle,true,message);
    }

    @Transactional
    public NoteResponse editNote(NoteRequest noteRequest,String noteTitle,String courseTitle,MultipartFile file)

    {
        /* Retrieve Course object from database using title */
        Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        Map<String,Object> message = new HashMap<>();
        /* Return error if course is not found*/
        if(course.isEmpty())
        {
            message.put("status","No course "+courseTitle+" found");
            return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
        }
        /*
         Retrieve Note object using note title
         return error response if not found
         */
        Optional<Note> note = noteRepo.findByTitle(noteTitle);
        if(note.isEmpty())
        {
            message.put("status","Note not found");
            message.put("details","Note "+noteTitle+" not found on Course "+courseTitle);
            return contentMapper.toNoteResponseError(noteTitle,true,message);
        }
        /*
        check new note title for duplication
        return error response if found
        */
        if(!note.get().getTitle().equals(noteTitle))
        {
            boolean duplicateNote = noteRepo.existsByTitleAndCourse(noteTitle,course.get());
            if(duplicateNote) {
                message.put("status", "Note title error");
                message.put("details", "Note " + noteTitle + " already exists");
                return contentMapper.toNoteResponseError(noteTitle, true, message);
            }
        }
        /*
         Map Note dto to entity
         assign course to note
         set original note id
         */
        Note modifiedNote = contentMapper.toNoteEntity(noteRequest);
        modifiedNote.setCourse(course.get());
        modifiedNote.setId(note.get().getId());

        /*
        check if the content is updated
        if not skip the file saving process
        */
        boolean isHashEqual = isHashEqual(note.get().getContentHash(),file);
        if(!isHashEqual )
        {
            modifiedNote = saveContentFileAndProperties(modifiedNote,file,noteDirectory);
        }

        Note updatedNote = noteRepo.save(modifiedNote);
        message.put("status","Note updated");
        return contentMapper.toNoteResponseSuccess(updatedNote,false,message);

    }

    @Transactional
    public NoteResponse removeNote(String courseTitle,String contentTitle) throws NoteDeletionFailedException
    {
            /* Retrieve Course from title
            check is course exists
            if not found throw an exception
             */
            Course course = courseRepo.findByTitle(courseTitle)
                    .orElseThrow(
                            () -> new CourseNotFoundException("Course " + courseTitle + " not found to delete note " + contentTitle)
                    );

            Map<String, Object> message = new HashMap<>();
            /* Retrieve Note from title and course
            if not found throw an exception
            if found delete the note from repository
             */
            Note note = noteRepo.findByTitleAndCourse(contentTitle, course)
                    .orElseThrow(
                            () -> new NoteNotFoundException("No note "+ contentTitle +" found on course" + courseTitle)
                    );
            try{
            noteRepo.delete(note);
            message.put("status", "Note deleted");
            return contentMapper.toNoteResponseSuccess(note, false, message);
        }
        catch(DataAccessException ex)
        {
            logger.error("Note delete error : {}",ex.getMessage());
            throw new NoteDeletionFailedException("DataBase error : Failed to delete note "+note.getTitle(),ex.getCause());
        }

    }


       /*
        Generate unique file url
        save to the directory
        save content properties i.e. url,type,size
        returns null object if operation fails and content object if succeeds
         */
    @Transactional
    public <T extends Content> T saveContentFileAndProperties(T content, MultipartFile file, String uploadDirectory)

    {
        try {
            // Ensure directory exists
            Path directoryPath = Paths.get(uploadDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);  // Create directory if not exists
            }

            String newHash = calculateHash(file);

            //UPDATE CASE → same file uploaded
            if (content.getContentHash() != null &&
                    content.getContentHash().equals(newHash)) {

                logger.info("Same file detected, skipping save");
                return content;
            }

            //  Different file → delete old one
            if (content.getContentUrl() != null) {
                Path oldFile = directoryPath.resolve(content.getContentUrl());
                Files.deleteIfExists(oldFile);
            }

            // Save new file
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID() + "." + extension;

            Path newFilePath = directoryPath.resolve(uniqueFileName);
            Files.write(newFilePath, file.getBytes());

            //save content file properties
            content.setContentUrl(uniqueFileName);
            content.setContentType(file.getContentType());
            content.setContentSize(file.getSize());
            content.setContentHash(newHash);
            return content;
        } catch (Exception ex) {
            logger.error("File upload failed : {}",ex.getLocalizedMessage());
            throw new FileUploadFailedException("File upload failed");
        }

    }

        private  boolean isHashEqual(String contentHash,MultipartFile file){
        try {
            String newHash = calculateHash(file);
            return contentHash != null &&
                    contentHash.equals(newHash);
        }
        catch(Exception ex)
        {
            logger.error("File hash comparison error : {}",ex.getLocalizedMessage());
            throw new FileUploadFailedException("File hash comparison error");
        }
        }


    private String calculateHash(MultipartFile file)  {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        }
        catch(Exception ex)
        {
            logger.error("File hash calculation error : {}",ex.getLocalizedMessage());
            throw new FileUploadFailedException("File hash calculation error");
        }
    }


}
