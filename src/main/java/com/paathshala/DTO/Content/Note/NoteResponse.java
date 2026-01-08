package com.paathshala.DTO.Content.Note;

import com.paathshala.DTO.Content.ContentResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class NoteResponse extends ContentResponse {


    public NoteResponse(String title, boolean isError, Map<String, Object> message) {
        super(title,isError,message);
    }
}
