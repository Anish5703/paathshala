package com.paathshala.service;

import com.paathshala.dto.ApiMessage;
import com.paathshala.dto.course.CourseDetails;
import com.paathshala.dto.course.CourseRequest;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import com.paathshala.exception.FileUploadFailedException;
import com.paathshala.exception.category.CategoryNotFoundException;
import com.paathshala.exception.course.*;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CategoryRepo;
import com.paathshala.repository.CourseRepo;
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
public class CourseService {

    private final CourseRepo courseRepo;


    private final CategoryRepo categoryRepo;

    private final CourseMapper courseMapper;

    @Value("${course.dir}")
    private String courseDirectory;

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    public CourseService(CourseRepo courseRepo,CategoryRepo categoryRepo,CourseMapper courseMapper)
    {
        this.courseRepo = courseRepo;
        this.categoryRepo = categoryRepo;
        this.courseMapper= courseMapper;
    }


    public CourseDetails getCourse(String courseTitle)
    {
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException("Course not found")
                );
        return courseMapper.toCourseDetails(course);
    }

    @Transactional
    public List<CourseDetails> getAllCourse()
    {
        List<Course> courses = courseRepo.findAll();
        if(courses.isEmpty())
        {
            throw new CourseNotFoundException("No Course found");
        }
        return courseMapper.toCourseDetailsList(courses);
    }



   @Transactional
    public CourseResponse addCourse(CourseRequest request, MultipartFile image)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");

       if(courseRepo.existsByTitle(request.getTitle()))
           throw new CourseDuplicateFoundException(String.format("Failed to save course : Course '%s' already exists",request.getTitle()));


       Course course = courseMapper.toEntity(request);
        //set category to the course
        course.setCategory(findCategory(request.getCategoryTitle()));

        try {
            String imageUrl = storeImage(image,courseDirectory,null);
            course.setImageUrl(imageUrl);
            Course savedCourse = courseRepo.save(course);
            ApiMessage message = new ApiMessage();
            message.setStatus("Course added");
            message.setDetails(String.format("Course '%s' created successfully",request.getTitle()));
            return courseMapper.toCourseResponseSuccess(savedCourse,message);
        }
        catch(DataAccessException | IOException ex)
        {
            logger.error(ErrorType.COURSE_NOT_SAVED.toString(),ex);
            throw new CourseSaveFailedException(String.format("Failed to save course '%s' : Database Error",course.getTitle()));
        }


    }
    @Transactional
    public CourseResponse updateCourse(CourseRequest request, String courseTitle)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Failed to update course : Course '%s' not found",courseTitle))
                );
        Course modifiedCourse = prepareCourseUpdate(request,course);

       try {
           Course updatedCourse = courseRepo.save(modifiedCourse);
           ApiMessage message = new ApiMessage();
           message.setStatus("Course updated");
           message.setDetails(String.format("Course '%s' updated successfully",courseTitle));
           return courseMapper.toCourseResponseSuccess(updatedCourse, message);
       }
       catch(DataAccessException ex)
       {
           logger.error(ErrorType.COURSE_NOT_UPDATED.toString(),ex);
           throw new CourseUpdateFailedException(String.format("Failed to update course '%s' : Database error",courseTitle));
       }

    }
    @Transactional
    public CourseResponse removeCourse(String courseTitle)
    {
        if(courseTitle.isEmpty())
            throw new IllegalArgumentException("Course cannot be null");
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Failed to delete course : Course '%s' not found",courseTitle))
                ) ;
       try {
           courseRepo.deleteById(course.getId());
           ApiMessage message = new ApiMessage();

           message.setStatus("Course removed");
           message.setStatus(String.format("Course '%s' deleted successfully",courseTitle));
           return courseMapper.toCourseResponseSuccess(course,message);
       }
       catch(DataAccessException ex)
       {
           logger.info(ErrorType.COURSE_NOT_DELETED.toString(),ex);
           throw new CourseDeleteFailedException(String.format("Failed to delete course '%s' : Database Error",courseTitle));
       }
    }

    public Category findCategory(String categoryTitle)
    {
        return categoryRepo.findByTitle(categoryTitle)
                .orElseThrow(
                        () -> new CategoryNotFoundException(String.format("No Category '%s' found",categoryTitle))
                );



    }

    private String storeImage(
            MultipartFile file,
            String uploadDirectory,
            String oldFileName
    ) throws IOException {

        // Convert directory to relative path
        Path directoryPath = Paths.get(uploadDirectory);
        logger.info("Resolved upload directory: {}", directoryPath);

        // Create directories if they do not exist
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Delete old file if provided
        if (oldFileName != null && !oldFileName.isEmpty()) {
            Path oldFilePath = directoryPath.resolve(oldFileName);
            logger.info("Deleted old file if exists: {}", oldFilePath);
            Files.deleteIfExists(oldFilePath);
        }
        // Check file is not empty
        if (file.isEmpty()) {
            throw new IOException("Uploaded file is empty!");
        }

        // Get file extension
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.') + 1);
        }

        // Generate unique filename
        String uniqueFileName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);

        // Save the file to disk
        Path filePath = directoryPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());
        logger.info("Image saved successfully at: {}", filePath.toAbsolutePath());

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

    private Course prepareCourseUpdate(CourseRequest request,Course course)
    {
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setCategory(findCategory(request.getCategoryTitle()));
        course.setPublished(request.isPublished());
        course.setImageUrl(request.getImageUrl());
        course.setEstimatedTime(request.getEstimatedTime());
        return course;
    }

}
