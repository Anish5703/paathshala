package com.paathshala.mapper;

import com.paathshala.DTO.Category.CategoryResponse;
import com.paathshala.DTO.Course.CourseRequest;
import com.paathshala.DTO.Course.CourseResponse;
import com.paathshala.entity.Category;
import com.paathshala.entity.Course;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CourseMapper {

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
        response.setCategory(course.getCategory());
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
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getCategory(),
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
