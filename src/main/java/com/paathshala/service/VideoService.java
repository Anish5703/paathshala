package com.paathshala.service;

import com.paathshala.dto.content.Note.NoteDetails;
import com.paathshala.dto.content.Note.NoteRequest;
import com.paathshala.dto.content.Note.NoteResponse;
import com.paathshala.dto.content.Video.VideoDetails;
import com.paathshala.dto.content.Video.VideoRequest;
import com.paathshala.dto.content.Video.VideoResponse;
import com.paathshala.entity.Course;
import com.paathshala.entity.Note;
import com.paathshala.entity.Video;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.exception.content.*;
import com.paathshala.mapper.ContentMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.VideoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class VideoService {

    private final CourseRepo courseRepo;
    private final VideoRepo videoRepo;
    private final Logger logger;
    private final ContentMapper contentMapper;
    private final ContentService contentService;

    @Value("${video.dir}")
    private String videoDirectory;

    public VideoService(CourseRepo courseRepo,VideoRepo videoRepo,ContentMapper contentMapper,ContentService contentService)
    {
        this.courseRepo = courseRepo;
        this.videoRepo = videoRepo;
        logger = LoggerFactory.getLogger(VideoService.class);
        this.contentMapper = contentMapper;
        this.contentService = contentService;
    }

    @Transactional
    public VideoResponse addVideo(VideoRequest videoRequest, String courseTitle, MultipartFile file)  {

        logger.info("Video Details : Video exists {}  : Video Size {}",!file.isEmpty(),file.getSize());
       /*
         Retrieve course object from database using title
         */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );

        /*
        Find videos linked to the course
        Search for video title duplication
         */

        boolean isVideoDuplicate = videoRepo.existsByTitleAndCourse(videoRequest.getTitle(),course);
        if(isVideoDuplicate)
        {
            String videoTitle = videoRequest.getTitle();
            throw new ContentDuplicateFoundException(String.format("Add Video '%s' failed : Video '%s' already exists on Course '%s'",videoTitle,videoTitle,courseTitle));
        }

        /*
         Map Request to Entity
         Save course to the entity
        */
        Video video = contentMapper.toVideoEntity(videoRequest);
        video.setCourse(course);

        /* Save file and assigned file properties */
        video= contentService.saveContentFileAndProperties(video,file,videoDirectory);

         /*
         Save video in the database and return success response
         else throw an exception
         */
        try {
            video = videoRepo.save(video);
            String message = "Video created successfully";
            return contentMapper.toVideoResponseSuccess(video, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_SAVED.toString(),ex.getMessage());
            throw new ContentSaveFailedException("Video Save Failed : Database Error");
        }
    }


    public List<VideoDetails> getVideoList(String courseTitle)
    {
        /*
         Retrieve course object from database using title
         */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );
        /*
          return the video List*/
        List<Video> videos = videoRepo.findByCourse(course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Course '%s' has no videos",courseTitle))
                );
        return contentMapper.toVideoDetailsList(videos);

    }
    @Transactional
    public VideoResponse getVideoByTitle(String videoTitle,String courseTitle)
    {
        /* Retrieve course object from database using title */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );

        /*
        Retrieve Video linked to the course
        throw an exception if not found any
        else return success response
         */

        Video video = videoRepo.findByTitleAndCourse(videoTitle,course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Video '%s' not found om Course '%s'",videoTitle,courseTitle))
                );
        String message = "Video found";
        return contentMapper.toVideoResponseSuccess(video,message);

    }

    @Transactional
    public VideoResponse updateVideo(VideoRequest videoRequest, String videoTitle, String courseTitle, MultipartFile file)

    {
        /*
        Retrieve Course object from database using course title
        throw exception if not found
        */
        Course course =  courseRepo.findByTitle(courseTitle).orElseThrow(
                () -> new CourseNotFoundException(String.format("Update Note '%s' failed : Course '%s' not found",videoTitle,courseTitle))
        );

        /*
         Retrieve video object using video title
         return error response if not found
         */
        Video video = videoRepo.findByTitleAndCourse(videoTitle,course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Update Video '%s' failed : Video '%s' not found on Course '%s'",videoTitle,videoTitle,courseTitle ))
                );
        /*
        check new video title for duplication
        throw exception  if found
        */
        if(!video.getTitle().equals(videoTitle))
        {
            boolean duplicateVideoExists = videoRepo.existsByTitleAndCourse(videoTitle,course);
            if(duplicateVideoExists)
                throw new ContentDuplicateFoundException(String.format("Update Video '%s' failed : Video '%s' already exists on Course '%s",videoTitle,videoTitle,courseTitle));
        }
        /*
         Map video dto to entity
         assign course to video
         set original video id
         */
        Video modifiedVideo = contentMapper.toVideoEntity(videoRequest);
        modifiedVideo.setCourse(course);
        modifiedVideo.setId(video.getId());

        /*
        check if the content is updated
        if not skip the file saving process
        */
        if(file != null) {
            boolean isHashEqual = contentService.isHashEqual(video.getContentHash(), file);
            if (!isHashEqual) {
                modifiedVideo = contentService.saveContentFileAndProperties(modifiedVideo, file, videoDirectory);
            }
            else {
                modifiedVideo = video;
                modifiedVideo.setDescription(videoRequest.getDescription());
            }
        }
        else{
            modifiedVideo = video;
            modifiedVideo.setDescription(videoRequest.getDescription());
        }
        try {
            Video updatedVideo = videoRepo.save(modifiedVideo);
            String message = ("Video updated successfully");
            return contentMapper.toVideoResponseSuccess(updatedVideo, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_UPDATED.toString(),ex.getMessage());
            throw new ContentUpdateFailedException(String.format("Update Video '%s' Failed : Database Error",videoTitle));
        }

    }

    @Transactional
    public VideoResponse removeVideo(String courseTitle,String videoTitle) throws ContentDeleteFailedException
    {
            /* Retrieve Course from title
            check if course exists
            not found throw an exception
             */
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found : Delete note '%s' failed",courseTitle,videoTitle))
                );

            /* Retrieve video from title and course
            if not found throw an exception
            if found delete the video from repository
             */
        Video video = videoRepo.findByTitleAndCourse(videoTitle, course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Video '%s' not found on course '%s'",videoTitle,courseTitle))
                );
        try{
            videoRepo.deleteById(video.getId());
            String message = "Video removed successfully";
            return contentMapper.toVideoResponseSuccess(video,message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_DELETED.toString(),ex);
            throw new ContentDeleteFailedException(String.format("DataBase error : Failed to delete video '%s'",video.getTitle()),ex.getCause());
        }

    }


}
