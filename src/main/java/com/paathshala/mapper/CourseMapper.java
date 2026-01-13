package com.paathshala.mapper;

import com.paathshala.dto.ApiMessage;
import com.paathshala.dto.course.CourseDetails;
import com.paathshala.dto.course.CourseRequest;
import com.paathshala.dto.course.CourseResponse;
import com.paathshala.entity.Course;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CourseMapper {


    public CourseResponse toCourseResponseSuccess(Course course,ApiMessage message)
    {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setCategoryTitle(course.getCategory().getTitle());
        response.setPrice(course.getPrice());
        response.setDescription(course.getDescription());
        response.setPublished(course.isPublished());
        response.setEstimatedTime(course.getEstimatedTime());
        response.setMessage(message);
        return response;
    }
    public CourseResponse toCourseResponse(Course course)
    {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getCategory().getTitle(),
                course.getPrice(),
                course.getDescription(),
                course.isPublished(),
                course.getEstimatedTime()
                );
    }
    public CourseDetails toCourseDetails(Course course)
    {
        return new CourseDetails(
                course.getTitle(),
                course.getCategory().getTitle(),
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
