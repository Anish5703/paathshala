package com.paathshala.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="note_tbl")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Note extends Content{


    public Note(String title,Course course,String description,String contentUrl,String contentType,Long contentSize)
    {
        super(title,course,description,contentUrl,contentType,contentSize);

    }
}
