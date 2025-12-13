package com.paathshala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="course_tbl")
public class Course {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column(unique = true,nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private double price;

    private String description;

    private boolean isPublished;

    private int estimatedTime;

 public Course(String title,Category category,double price,String description,boolean isPublished,int estimatedTime)
 {
     this.title = title;
     this.category = category;
     this.price = price;
     this.description = description;
     this.isPublished = isPublished;
     this.estimatedTime = estimatedTime;
 }
    

}
