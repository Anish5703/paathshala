package com.paathshala.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name="enrollment_tbl")
@NoArgsConstructor
@Data
public class Enrollment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;

    @CreationTimestamp
    private LocalDate createdTime;

    private boolean paid;
    private String sessionId;
    public Enrollment(User user,Course course)
    {
        this.user = user;
        this.course = course;
    }

    public Enrollment(User user,Course course,boolean paid,String sessionId)
    {
       this.user = user;
       this.course = course;
       this.paid = paid;
       this.sessionId = sessionId;

    }




}
