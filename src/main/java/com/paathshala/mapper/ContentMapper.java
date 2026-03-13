package com.paathshala.mapper;

import com.paathshala.dto.ApiMessage;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionDetails;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionRequest;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionResponse;
import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.dto.content.Video.VideoDetails;
import com.paathshala.dto.content.Video.VideoRequest;
import com.paathshala.dto.content.Video.VideoResponse;
import com.paathshala.entity.ModelQuestion;
import com.paathshala.entity.Note;
import com.paathshala.entity.Video;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ContentMapper {



    public Note toNoteEntity(NoteRequest request)
    {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        return note;
    }


    public NoteResponse toNoteResponseSuccess(Note note,String message)
    {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setDescription(note.getDescription());
        response.setCourseTitle(note.getCourse().getTitle());
        response.setContentUrl(note.getContentUrl());
        response.setContentType(note.getContentType());
        response.setContentSize(note.getContentSize());
        response.setMessage(message);
        return response;

    }
    public NoteDetails toNoteDetails(Note note)
    {
        NoteDetails noteDetails = new NoteDetails();
        noteDetails.setId(note.getId());
        noteDetails.setTitle(note.getTitle());
        noteDetails.setCourseTitle(note.getCourse().getTitle());
        noteDetails.setDescription(note.getDescription());
        noteDetails.setCreatedAt(note.getCreatedAt());
        noteDetails.setContentUrl(note.getContentUrl());
        noteDetails.setContentType(note.getContentType());
        noteDetails.setContentSize(note.getContentSize());
        return noteDetails;
    }

    public List<NoteDetails> toNoteDetailsList(List<Note> notes)
    {
        if(notes.isEmpty()) return null;
        List<NoteDetails> noteDetailsList = new ArrayList<>();
        for(Note note : notes )
        {
            noteDetailsList.add(toNoteDetails(note));
        }
        return noteDetailsList;
    }

    public Video toVideoEntity(VideoRequest request)
    {
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        return video;
    }
    public VideoResponse toVideoResponseSuccess(Video video, String message)
    {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setCourseTitle(video.getCourse().getTitle());
        response.setContentUrl(video.getContentUrl());
        response.setContentType(video.getContentType());
        response.setContentSize(video.getContentSize());
        response.setMessage(message);
        return response;

    }
    public VideoDetails toVideoDetails(Video video)
    {
        VideoDetails videoDetails = new VideoDetails();
        videoDetails.setId(video.getId());
        videoDetails.setTitle(video.getTitle());
        videoDetails.setCourseTitle(video.getCourse().getTitle());
        videoDetails.setDescription(video.getDescription());
        videoDetails.setCreatedAt(video.getCreatedAt());
        videoDetails.setContentUrl(video.getContentUrl());
        videoDetails.setContentType(video.getContentType());
        videoDetails.setContentSize(video.getContentSize());
        return videoDetails;
    }
    public List<VideoDetails> toVideoDetailsList(List<Video> videos)
    {
        if(videos.isEmpty()) return null;
        List<VideoDetails> videoDetailsList = new ArrayList<>();
        for(Video video : videos )
        {
            videoDetailsList.add(toVideoDetails(video));
        }
        return videoDetailsList;
    }

    public ModelQuestion toModelQuestionEntity(ModelQuestionRequest request)
    {
        ModelQuestion modelQuestion = new ModelQuestion();
        modelQuestion.setTitle(request.getTitle());
        modelQuestion.setDescription(request.getDescription());
        return modelQuestion;
    }
    public ModelQuestionResponse toModelQuestionResponseSuccess(ModelQuestion modelQuestion, String message)
    {
        ModelQuestionResponse response = new ModelQuestionResponse();
        response.setId(modelQuestion.getId());
        response.setTitle(modelQuestion.getTitle());
        response.setDescription(modelQuestion.getDescription());
        response.setCourseTitle(modelQuestion.getCourse().getTitle());
        response.setContentUrl(modelQuestion.getContentUrl());
        response.setContentType(modelQuestion.getContentType());
        response.setContentSize(modelQuestion.getContentSize());
        response.setMessage(message);
        return response;

    }
    public ModelQuestionDetails toModelQuestionDetails(ModelQuestion modelQuestion)
    {
        ModelQuestionDetails modelQuestionDetails = new ModelQuestionDetails();
        modelQuestionDetails.setId(modelQuestion.getId());
        modelQuestionDetails.setTitle(modelQuestion.getTitle());
        modelQuestionDetails.setCourseTitle(modelQuestion.getCourse().getTitle());
        modelQuestionDetails.setDescription(modelQuestion.getDescription());
        modelQuestionDetails.setCreatedAt(modelQuestion.getCreatedAt());
        modelQuestionDetails.setContentUrl(modelQuestion.getContentUrl());
        modelQuestionDetails.setContentType(modelQuestion.getContentType());
        modelQuestionDetails.setContentSize(modelQuestion.getContentSize());
        return modelQuestionDetails;
    }
    public List<ModelQuestionDetails> toModelQuestionDetailsList(List<ModelQuestion> modelQuestions)
    {
        if(modelQuestions.isEmpty()) return null;
        List<ModelQuestionDetails> modelQUestionDetailsList = new ArrayList<>();
        for(ModelQuestion modelQuestion : modelQuestions )
        {
            modelQUestionDetailsList.add(toModelQuestionDetails(modelQuestion));
        }
        return modelQUestionDetailsList;
    }


}
