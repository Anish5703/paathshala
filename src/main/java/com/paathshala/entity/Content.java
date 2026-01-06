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
@AllArgsConstructor
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
    @JoinColumn(name="content_id")
    private Course course;
    private String description;
    @CreationTimestamp
    private LocalDateTime createdAt;



}
