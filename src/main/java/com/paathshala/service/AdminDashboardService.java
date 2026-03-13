package com.paathshala.service;

import com.paathshala.dto.StudentDetails;
import com.paathshala.entity.Student;
import com.paathshala.entity.User;
import com.paathshala.mapper.UserMapper;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.EnrollmentRepo;
import com.paathshala.repository.StudentRepo;
import com.paathshala.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    private final CourseRepo courseRepo;
    private final StudentRepo studentRepo;
    private final EnrollmentRepo enrollmentRepo;

    public AdminDashboardService(StudentRepo studentRepo,CourseRepo courseRepo,EnrollmentRepo enrollmentRepo)
    {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    public long getTotalNumberOfStudent()
    {
        return studentRepo.count();
    }
    public long getTotalNumberOfEnrolledStudent()
    {
        return enrollmentRepo.totalActiveUsers();
    }

    public List<StudentDetails> getAllStudents()
    {
        List<Student> students = studentRepo.findAll();
        if(students.isEmpty())
            throw new UsernameNotFoundException("No user found");
        else
           return UserMapper.toStudentDetailsList(students);


    }

}
