package com.paathshala.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="modelQuestion_tbl")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelQuestion extends Content{
    @Column(nullable = false)
    private String contentUrl;
    private String contentType;
    private Byte contentSize;

    public ModelQuestion(String title,Course course,String description,String contentUrl,String contentType,Byte contentSize)
    {
        super(title,course,description);
        this.contentUrl=contentUrl;
        this.contentType=contentType;
        this.contentSize=contentSize;
    }
}
