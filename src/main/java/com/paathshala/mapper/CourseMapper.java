package com.paathshala.mapper;

import com.paathshala.dto.category.CategoryDetails;
import com.paathshala.dto.category.CategoryResponse;
import com.paathshala.dto.course.CourseDetails;
import com.paathshala.dto.course.CourseRequest;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CourseMapper {

    @Autowired
    CategoryMapper categoryMapper;

    public CourseResponse toCourseResponseError(Course course, boolean error, Map<String,Object> message)
    {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setError(error);
        response.setMessage(message);
       return response;
    }
    public CourseResponse toCourseResponseSuccess(Course course,boolean error,Map<String,Object> message)
    {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(course.getCategory());
        response.setCategory(categoryResponse);
        response.setPrice(course.getPrice());
        response.setDescription(course.getDescription());
        response.setPublished(course.isPublished());
        response.setEstimatedTime(course.getEstimatedTime());
        response.setError(error);
        response.setMessage(message);
        return response;
    }
    public CourseResponse toCourseResponse(Course course)
    {
        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(course.getCategory());
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                categoryResponse,
                course.getPrice(),
                course.getDescription(),
                course.isPublished(),
                course.getEstimatedTime()
                );
    }
    public CourseDetails toCourseDetails(Course course)
    {
        CategoryDetails categoryDetails = categoryMapper.toCategoryDetails(course.getCategory());
        return new CourseDetails(
                course.getId(),
                course.getTitle(),
                categoryDetails,
                course.getPrice(),
                course.getDescription(),
                course.isPublished(),
                course.getEstimatedTime()
        );
    }

    public List<CourseResponse> toCourseResponseList(List<Course> courses)
    {
        List<CourseResponse> response = new ArrayList<>();
        for(Course course : courses)
        {
            response.add(toCourseResponse(course));
        }
        return response;
    }

    public List<CourseDetails> toCourseDetailsList(List<Course> courses)
    {
        List<CourseDetails> response = new ArrayList<>();
        for(Course course : courses)
        {
            response.add(toCourseDetails(course));
        }
        return response;
    }


    public Course toEntity(CourseRequest request)
    {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setPrice(request.getPrice());
        course.setDescription(request.getDescription());
        course.setPublished(request.isPublished());
        course.setEstimatedTime(request.getEstimatedTime());
       return course;

    }
}
