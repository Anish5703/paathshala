package com.paathshala.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Inheritance(strategy=InheritanceType.JOINED)
@Entity
@Table(name="content_tbl")
@NoArgsConstructor
@Getter
@Setter
public abstract class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String title;
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
    private String description;
    @Column(nullable = false)
    private String contentUrl;
    private String contentType;
    private Long contentSize;
    private String contentHash;
    @CreationTimestamp
    private LocalDateTime createdAt;

public Content(String title,Course course,String description,String contentUrl,String contentType,Long contentSize)
{
    this.title=title;
    this.course=course;
    this.description=description;
    this.contentUrl=contentUrl;
    this.contentSize=contentSize;
    this.contentType=contentType;
}

}
