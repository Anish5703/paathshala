package com.paathshala.repository;

import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepo extends JpaRepository<Note,Integer> {

    Optional<List<Note>> findByCourse(Course course);
    Optional<Note> findByTitle(String noteTitle);
    Optional<Note> findByTitleAndCourse(String noteTitle,Course course);
    boolean existsByTitleAndCourse(String noteTitle,Course course);

}
