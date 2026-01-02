package com.paathshala.service;

import com.paathshala.DTO.Course.CourseRequest;
import com.paathshala.DTO.Course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import com.paathshala.mapper.CourseMapper;
import com.paathshala.repository.CategoryRepo;
import com.paathshala.repository.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CourseMapper courseMapper;



    @Transactional
    public List<CourseResponse> getAllCourse()
    {
        List<Course> courses = courseRepo.findAll();
        if(courses.isEmpty())
        {
            return null;
        }
        return courseMapper.toCourseResponseList(courses);
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
    public CourseResponse editCourse(CourseRequest request)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");
        Optional<Course> course = courseRepo.findById(request.getId());
        Map<String,Object> message = new HashMap<>();
        if(course.isEmpty())
        {
            message.put("status","No Course found with id:"+request.getId());
            course.get().setId(request.getId());
            return courseMapper.toCourseResponseError(course.get(),true,message);
        }

        Course modifiedCourse = courseMapper.toEntity(request);
        //set new category to the course
        modifiedCourse.setCategory(findCategory(request.getCategoryId()));

        Optional<Course> savedCourse = courseRepo.findByTitle(request.getTitle());

        if(savedCourse.isPresent() && savedCourse.get().getId() != request.getId())
        {
            message.put("status","Category title duplication");
            return courseMapper.toCourseResponseError(modifiedCourse,true,message);
        }
        Course updatedCourse = courseRepo.save(modifiedCourse);
        message.put("status","Modified successfully");
        return courseMapper.toCourseResponseSuccess(updatedCourse,false,message);

    }
    @Transactional
    public CourseResponse removeCourse(CourseRequest request)
    {
        if(request==null)
            throw new IllegalArgumentException("Course cannot be null");
        Optional<Course> course = courseRepo.findById(request.getId());
        Map<String,Object> message = new HashMap<>();
        if(course.isEmpty())
        {
            course.get().setId(request.getId());
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
