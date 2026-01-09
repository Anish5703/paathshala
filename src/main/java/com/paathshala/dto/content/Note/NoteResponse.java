package com.paathshala.dto.content.Note;

import com.paathshala.dto.content.ContentResponse;
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
