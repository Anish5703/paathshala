package com.paathshala.service;

import com.paathshala.dto.content.ModelQuestion.ModelQuestionDetails;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionRequest;
import com.paathshala.dto.content.ModelQuestion.ModelQuestionResponse;
import com.paathshala.dto.content.Video.VideoDetails;
import com.paathshala.dto.content.Video.VideoRequest;
import com.paathshala.dto.content.Video.VideoResponse;
import com.paathshala.entity.Course;
import com.paathshala.entity.ModelQuestion;
import com.paathshala.entity.Video;
import com.paathshala.exception.content.*;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.mapper.ContentMapper;
import com.paathshala.model.ErrorType;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.ModelQuestionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ModelQuestionService {

    private final CourseRepo courseRepo;
    private final ModelQuestionRepo modelQuestionRepo;
    private final ContentService contentService;
    private final ContentMapper contentMapper;
    private final Logger logger;

    @Value("${modelQuestion.dir}")
    private String modelQuestionDirectory;

    public ModelQuestionService(CourseRepo courseRepo,ModelQuestionRepo modelQuestionRepo,ContentService contentService,ContentMapper contentMapper)
    {
        this.courseRepo = courseRepo;
        this.modelQuestionRepo = modelQuestionRepo;
        this.contentService = contentService;
        this.contentMapper = contentMapper;
        this.logger = LoggerFactory.getLogger(ModelQuestionService.class);
    }

    @Transactional
    public ModelQuestionResponse addModelQuestion(ModelQuestionRequest modelQuestionRequest, String courseTitle, MultipartFile file)  {
       /*
         Retrieve course object from database using title
         */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );

        /*
        Find model questions linked to the course
        Search for model question title duplication
         */

        boolean isModelQuestionDuplicate = modelQuestionRepo.existsByTitleAndCourse(modelQuestionRequest.getTitle(),course);
        if(isModelQuestionDuplicate)
        {
            String videoTitle = modelQuestionRequest.getTitle();
            throw new ContentDuplicateFoundException(String.format("Add Model Question '%s' failed : Model Question '%s' already exists on Course '%s'",videoTitle,videoTitle,courseTitle));
        }

        /*
         Map Request to Entity
         Save course to the entity
        */
        ModelQuestion modelQuestion = contentMapper.toModelQuestionEntity(modelQuestionRequest);
        modelQuestion.setCourse(course);

        /* Save file and assigned file properties */
        modelQuestion= contentService.saveContentFileAndProperties(modelQuestion,file,modelQuestionDirectory);

         /*
         Save model question in the database and return success response
         else throw an exception
         */
        try {
            modelQuestion = modelQuestionRepo.save(modelQuestion);
            String message = "Model Question created successfully";
            return contentMapper.toModelQuestionResponseSuccess(modelQuestion, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_SAVED.toString(),ex.getMessage());
            throw new ContentSaveFailedException("Model Question Save Failed : Database Error");
        }
    }


    public List<ModelQuestionDetails> getModelQuestionList(String courseTitle)
    {
        /*
         Retrieve course object from database using title
         */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );
        /*
          return the model question details List*/
        List<ModelQuestion> modelQuestions = modelQuestionRepo.findByCourse(course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Course '%s' has no model questions",courseTitle))
                );
        return contentMapper.toModelQuestionDetailsList(modelQuestions);

    }
    @Transactional
    public ModelQuestionResponse getModelQuestionByTitle(String modelQuestionTitle,String courseTitle)
    {
        /* Retrieve course object from database using title */
        Course course =  courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found",courseTitle))
                );

        /*
        Retrieve model question linked to the course
        throw an exception if not found any
        else return success response
         */

        ModelQuestion modelQuestion = modelQuestionRepo.findByTitleAndCourse(modelQuestionTitle,course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Model Question '%s' not found om Course '%s'",modelQuestionTitle,courseTitle))
                );
        String message = "Model Question found";
        return contentMapper.toModelQuestionResponseSuccess(modelQuestion,message);

    }

    @Transactional
    public ModelQuestionResponse updateModelQuestion(ModelQuestionRequest modelQuestionRequest, String modelQuestionTitle, String courseTitle, MultipartFile file)

    {
        /*
        Retrieve Course object from database using course title
        throw exception if not found
        */
        Course course =  courseRepo.findByTitle(courseTitle).orElseThrow(
                () -> new CourseNotFoundException(String.format("Update Note '%s' failed : Course '%s' not found",modelQuestionTitle,courseTitle))
        );

        /*
         Retrieve model question object using model question title
         return error response if not found
         */
        ModelQuestion modelQuestion = modelQuestionRepo.findByTitleAndCourse(modelQuestionTitle,course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Update Model Question '%s' failed : Model Question '%s' not found on Course '%s'",modelQuestionTitle,modelQuestionTitle,courseTitle ))
                );
        /*
        check new model question title for duplication
        throw exception  if found
        */
        if(!modelQuestion.getTitle().equals(modelQuestionTitle))
        {
            boolean duplicateModelQuestionExists = modelQuestionRepo.existsByTitleAndCourse(modelQuestionTitle,course);
            if(duplicateModelQuestionExists)
                throw new ContentDuplicateFoundException(String.format("Update Model Question'%s' failed : Model Question '%s' already exists on Course '%s",modelQuestionTitle,modelQuestionTitle,courseTitle));
        }
        /*
         Map model question dto to entity
         assign course to model question
         set original model question id
         */
        ModelQuestion modifiedModelQuestion = contentMapper.toModelQuestionEntity(modelQuestionRequest);
        modifiedModelQuestion.setCourse(course);
        modifiedModelQuestion.setId(modelQuestion.getId());

        /*
        check if the content is updated
        if not skip the file saving process
        */
        if(file != null) {
            boolean isHashEqual = contentService.isHashEqual(modelQuestion.getContentHash(), file);
            if (!isHashEqual) {
                modifiedModelQuestion = contentService.saveContentFileAndProperties(modifiedModelQuestion, file, modelQuestionDirectory);
            }
            else {
                modifiedModelQuestion = modelQuestion;
                modifiedModelQuestion.setDescription(modelQuestionRequest.getDescription());
            }
        }
        else{
            modifiedModelQuestion = modelQuestion;
            modifiedModelQuestion.setDescription(modelQuestionRequest.getDescription());        }
        try {
            ModelQuestion updatedModelQuestion = modelQuestionRepo.save(modifiedModelQuestion);
            String message = ("Model Question updated successfully");
            return contentMapper.toModelQuestionResponseSuccess(updatedModelQuestion, message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_UPDATED.toString(),ex.getMessage());
            throw new ContentUpdateFailedException(String.format("Update Model Question '%s' Failed : Database Error",modelQuestionTitle));
        }

    }

    @Transactional
    public ModelQuestionResponse removeModelQuestion(String courseTitle,String modelQuestionTitle) throws ContentDeleteFailedException
    {
            /* Retrieve Course from title
            check if course exists
            not found throw an exception
             */
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(
                        () -> new CourseNotFoundException(String.format("Course '%s' not found : Delete note '%s' failed",courseTitle,modelQuestionTitle))
                );

            /* Retrieve model question from title and course
            if not found throw an exception
            if found delete the model question from repository
             */
        ModelQuestion modelQuestion = modelQuestionRepo.findByTitleAndCourse(modelQuestionTitle, course)
                .orElseThrow(
                        () -> new ContentNotFoundException(String.format("Model Question '%s' not found on course '%s'",modelQuestionTitle,courseTitle))
                );
        try{
            modelQuestionRepo.deleteById(modelQuestion.getId());
            String message = "Note removed successfully";
            return contentMapper.toModelQuestionResponseSuccess(modelQuestion,message);
        }
        catch(DataAccessException ex)
        {
            logger.error(ErrorType.CONTENT_NOT_DELETED.toString(),ex);
            throw new ContentDeleteFailedException(String.format("DataBase error : Failed to delete model question '%s'",modelQuestion.getTitle()),ex.getCause());
        }

    }

}
