package com.paathshala.service;

import com.paathshala.dto.course.CourseDetails;
import com.paathshala.dto.course.CourseRequest;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import com.paathshala.exception.category.CategoryNotFoundException;
import com.paathshala.exception.course.*;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CategoryRepo;
import com.paathshala.repository.CourseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CourseService {

    private final CourseRepo courseRepo;


    private final CategoryRepo categoryRepo;

    private final CourseMapper courseMapper;

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    public CourseService(CourseRepo courseRepo,CategoryRepo categoryRepo,CourseMapper courseMapper)
    {
        this.courseRepo = courseRepo;
        this.categoryRepo = categoryRepo;
        this.courseMapper= courseMapper;
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
    public CourseResponse addCourse(CourseRequest request)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");

       if(courseRepo.existsByTitle(request.getTitle()))
           throw new CourseDuplicateFoundException(String.format("Failed to save course : Course '%s' already exists",request.getTitle()));

        Map<String,Object> message = new HashMap<>();

       Course course = courseMapper.toEntity(request);
        //set category to the course
        course.setCategory(findCategory(request.getCategoryId()));

        try {
            Course savedCourse = courseRepo.save(course);
            message.put("status", "Course added");
            return courseMapper.toCourseResponseSuccess(savedCourse, false, message);
        }
        catch(DataAccessException ex)
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
        Map<String,Object> message = new HashMap<>();

        Course modifiedCourse = courseMapper.toEntity(request);
        //set new category to the course
        modifiedCourse.setCategory(findCategory(request.getCategoryId()));

       if(request.getTitle() != courseTitle)
       {
          boolean isTitleDuplicate = courseRepo.existsByTitle(request.getTitle());
          if(isTitleDuplicate)
              throw new CourseDuplicateFoundException("Failed to update course : Course '%s' already exists");
       }
       try {
           Course updatedCourse = courseRepo.save(modifiedCourse);
           message.put("status", "Modified successfully");
           return courseMapper.toCourseResponseSuccess(updatedCourse, false, message);
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
        Map<String,Object> message = new HashMap<>();
       try {
           courseRepo.deleteById(course.getId());
           message.put("status", "Course deleted");
           return courseMapper.toCourseResponseSuccess(course, false, message);
       }
       catch(DataAccessException ex)
       {
           logger.info(ErrorType.COURSE_NOT_DELETED.toString(),ex);
           throw new CourseDeleteFailedException(String.format("Failed to delete course '%s' : Database Error",courseTitle));
       }
    }

    public Category findCategory(int id)
    {
        return categoryRepo.findById(id)
                .orElseThrow(
                        () -> new CategoryNotFoundException(String.format("No Category '%d' found",id))
                );



    }

}
