package com.paathshala.service;

import com.paathshala.DTO.Content.ContentResponse;
import com.paathshala.DTO.Content.Note.NoteDetails;
import com.paathshala.DTO.Content.Note.NoteRequest;
import com.paathshala.DTO.Content.Note.NoteResponse;
import com.paathshala.entity.Content;
import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import com.paathshala.mapper.ContentMapper;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.NoteRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public NoteResponse addNote(NoteRequest noteRequest,String courseTitle,MultipartFile file) throws IOException {
        /* Find course object from database using title */
      Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        Map<String,Object> message = new HashMap<>();
        /* Return error if course not found*/
        if(course.isEmpty())
        {
            message.put("status","No course "+courseTitle+" found");
            return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
        }
        /*
        Find notes linked to the course
        Search for note title duplication
         */
     Optional<List<Note>> notes = noteRepo.findByCourse(course.get());
        if(notes.isPresent())
        {
            for(Note note:notes.get())
            {
                if(note.getTitle().equals(noteRequest.getTitle()))
                {
                    message.put("status","Note title duplication ");
                    message.put("details","Duplicate note title found in course "+courseTitle);
                    return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
                }
            }
        }
        /*
         Map Request to Entity
         Save course to the entity
        */
        Note note = contentMapper.toNoteEntity(noteRequest);
        note.setCourse(course.get());

        /* Save file and assigned file properties */
         note=saveContent(note,file,noteDirectory);

         if(note==null)
         {
             message.put("status","File upload error");
             return contentMapper.toNoteResponseError(noteRequest.getTitle(),true,message);
         }
         /* Save note the database and return success response */
        note = noteRepo.save(note);
        message.put("status","Note added");
        message.put("details","New note "+note.getTitle()+" added to the course "+courseTitle);
        return contentMapper.toNoteResponseSuccess(note,false,message);
    }


    public List<NoteDetails> getNoteList(String courseTitle)
    {
        /* Find course object from database using title */
        Optional<Course> course =  courseRepo.findByTitle(courseTitle);
        Map<String,Object> message = new HashMap<>();
        /* Return error if course is not found*/
        if(course.isEmpty()) return null;
        Optional<List<Note>> notes = noteRepo.findByCourse(course.get());
        if(notes.isEmpty()) return null;

        return contentMapper.toNoteDetailsList(notes.get());

    }
    public NoteResponse getNoteByTitle(String noteTitle,String courseTitle)
    {
        /* Find course object from database using title */
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
        Find notes linked to the course
        Search for matching note title
        return success response if found any
         */


        Optional<List<Note>> notes = noteRepo.findByCourse(course.get());
        if(notes.isPresent())
        {
            for(Note note:notes.get())
            {
                if(note.getTitle().equals(noteTitle))
                {
                    message.put("status","Note found");
                    return contentMapper.toNoteResponseSuccess(note,true,message);
                }
            }
        }
        /* return error response*/
        message.put("status","Note not found");
        message.put("details","Unable to locate note "+noteTitle+" in course "+courseTitle);
        return contentMapper.toNoteResponseError(noteTitle,true,message);
    }

       /*
        Generate unique file url
        save to the directory
        save content properties i.e url,type,size
        returns null object if operation fails and content object if succeeds
         */
    public <T extends Content> T saveContent(T content, MultipartFile file, String uploadDirectory)
    {
        try {
            // Ensure directory exists
            Path directoryPath = Paths.get(uploadDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);  // Create directory if not exists
            }

            String originalFileName = file.getOriginalFilename();
            //get unique file name
            String uniqueFileName = System.currentTimeMillis()+"_"+originalFileName;
            //set file name with  the default directory
            Path fileNameWithPath = Paths.get(uploadDirectory,uniqueFileName);
            //save file to the directory
            Files.write(fileNameWithPath, file.getBytes());

            //save content file properties
            content.setContentUrl(uniqueFileName);
            content.setContentType(file.getContentType());
            content.setContentSize(file.getSize());

            return content;
        }
        catch(Exception ex)
        {
            System.out.println(" File Not Uploaded ");
            logger.info(ex.getLocalizedMessage());
            return null;
        }

    }

}
