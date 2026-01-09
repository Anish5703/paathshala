package com.paathshala.service;

import com.paathshala.dto.course.CourseDetails;
import com.paathshala.dto.course.CourseRequest;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.repository.CategoryRepo;
import com.paathshala.repository.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CourseService {

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CourseMapper courseMapper;



    @Transactional
    public List<CourseDetails> getAllCourse()
    {
        List<Course> courses = courseRepo.findAll();
        if(courses.isEmpty())
        {
            return Collections.emptyList();
        }
        return courseMapper.toCourseDetailsList(courses);
    }



   @Transactional
    public CourseResponse addCourse(CourseRequest request)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");
        Optional<Course> duplicateCourse = courseRepo.findByTitle(request.getTitle());
        Map<String,Object> message = new HashMap<>();
        if(duplicateCourse.isPresent())
        {
            message.put("status","Course already exists");
            return courseMapper.toCourseResponseError(duplicateCourse.get(),true,message);
        }
       Course course = courseMapper.toEntity(request);
        //set category to the course
        course.setCategory(findCategory(request.getCategoryId()));

        Course savedCourse = courseRepo.save(course);
        message.put("status","Course added");
        return courseMapper.toCourseResponseSuccess(savedCourse,false,message);


    }
    @Transactional
    public CourseResponse editCourse(CourseRequest request,int courseId)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");
        Optional<Course> course = courseRepo.findById(courseId);
        Map<String,Object> message = new HashMap<>();
        if(course.isEmpty())
        {
            message.put("status","No Course found with id:"+courseId);
            course.get().setId(courseId);
            return courseMapper.toCourseResponseError(course.get(),true,message);
        }

        Course modifiedCourse = courseMapper.toEntity(request);
        //set new category to the course
        modifiedCourse.setCategory(findCategory(request.getCategoryId()));

        Optional<Course> savedCourse = courseRepo.findByTitle(request.getTitle());

        if(savedCourse.isPresent() && savedCourse.get().getId() != courseId)
        {
            message.put("status","Category title duplication");
            return courseMapper.toCourseResponseError(modifiedCourse,true,message);
        }
        Course updatedCourse = courseRepo.save(modifiedCourse);
        message.put("status","Modified successfully");
        return courseMapper.toCourseResponseSuccess(updatedCourse,false,message);

    }
    @Transactional
    public CourseResponse removeCourse(int courseId)
    {
        if(courseId<1)
            throw new IllegalArgumentException("Course cannot be null");
        Optional<Course> course = courseRepo.findById(courseId);
        Map<String,Object> message = new HashMap<>();
        if(course.isEmpty())
        {
            course.get().setId(courseId);
            message.put("status","No course found ");
            return courseMapper.toCourseResponseError(course.get(),true,message);
        }
        courseRepo.delete(course.get());
        message.put("status","Course deleted");
        return courseMapper.toCourseResponseSuccess(course.get(),false,message);
    }

    public Category findCategory(int id)
    {
        Optional<Category> category = categoryRepo.findById(id);
        if(category.isEmpty())
            return null;
        else
           return category.get();
    }

}
