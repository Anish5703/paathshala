package com.paathshala.service;

import com.paathshala.dto.mcq.*;
import com.paathshala.entity.Course;
import com.paathshala.entity.McqAttemptHistory;
import com.paathshala.entity.McqQuestion;
import com.paathshala.model.Difficulty;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.McqAttemptHistoryRepo;
import com.paathshala.repository.McqQuestionRepo;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class McqService {

    private final CourseRepo courseRepo;
    private final McqQuestionRepo mcqQuestionRepo;
    private final McqAttemptHistoryRepo mcqAttemptHistoryRepo;
    Logger logger;

    public McqService(CourseRepo courseRepo,McqQuestionRepo mcqQuestionRepo,McqAttemptHistoryRepo mcqAttemptHistoryRepo)
    {
        this.courseRepo = courseRepo;
        this.mcqQuestionRepo = mcqQuestionRepo;
        this.mcqAttemptHistoryRepo = mcqAttemptHistoryRepo;
    }

    public List<McqQuestionResponse> getQuestions(String courseTitle, String difficulty, int limit) {

        try {
            Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
            List<McqQuestion> questions = mcqQuestionRepo.findRandomByCourseAndDifficulty(
                    courseTitle, diff, PageRequest.of(0, limit));

            return questions.stream()
                    .map(q -> McqQuestionResponse.forStudent(
                            q.getId(), q.getCourse().getTitle(), q.getQuestion(),
                            q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                            q.getDifficulty().name()))
                    .collect(Collectors.toList());
        }
        catch(Exception e)
        {
            logger.error(e.getLocalizedMessage());
            throw new IllegalArgumentException("Mcq get Question error");
        }
    }

    @Transactional
    public McqResultResponse submitAnswers(String courseTitle, String username,
                                           McqSubmitRequest request) {
        try {
            Course course = courseRepo.findByTitle(courseTitle)
                    .orElseThrow(() -> new RuntimeException("Course not found: " + courseTitle));

            // Build a map of questionId → McqQuestion for fast lookup
            List<Integer> questionIds = request.getAttempts().stream()
                    .map(McqAttempt::getQuestionId)
                    .collect(Collectors.toList());
            Map<Integer, McqQuestion> questionMap = mcqQuestionRepo.findAllById(questionIds).stream()
                    .collect(Collectors.toMap(McqQuestion::getId, q -> q));

            // Grade each attempt
            List<McqAttemptResult> results = new ArrayList<>();
            int correct = 0;
            String currentDifficulty = "EASY";

            for (McqAttempt attempt : request.getAttempts()) {
                McqQuestion q = questionMap.get(attempt.getQuestionId());
                if (q == null) continue;

                currentDifficulty = q.getDifficulty().name();
                boolean isCorrect = q.getCorrectAnswer().equalsIgnoreCase(attempt.getSelectedAnswer());
                if (isCorrect) correct++;

                results.add(new McqAttemptResult(
                        q.getId(),
                        isCorrect,
                        q.getCorrectAnswer(),
                        attempt.getSelectedAnswer(),
                        q.getExplanation()
                ));
            }

            int total = results.size();
            int score = total > 0 ? (int) Math.round((correct * 100.0) / total) : 0;
            String nextDifficulty = calculateNextDifficulty(currentDifficulty, score);

            // Save attempt history
            McqAttemptHistory history = new McqAttemptHistory();
            history.setUsername(username);
            history.setCourse(course);
            history.setDifficulty(currentDifficulty);
            history.setCorrectAnswers(correct);
            history.setScore(score);
            history.setTotalQuestions(total);
            mcqAttemptHistoryRepo.save(history);

            return new McqResultResponse(total, correct, score, nextDifficulty, results);
        }
        catch(Exception e)
        {
            logger.error(e.getLocalizedMessage());
            throw new IllegalArgumentException("Mcq submit answer error");
        }
    }

    public List<McqQuestionResponse> getAllQuestions(String courseTitle) {
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseTitle));

        try {

            return mcqQuestionRepo.findByCourse_Id(course.getId()).stream()
                    .map(q -> McqQuestionResponse.forAdmin(
                            q.getId(), q.getCourse().getTitle(), q.getQuestion(),
                            q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                            q.getCorrectAnswer(), q.getDifficulty().name(), q.getExplanation()))
                    .collect(Collectors.toList());
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
            throw new IllegalArgumentException("Mcq get all questions error");
        }
    }

    @Transactional
    public McqQuestionResponse addQuestion(String courseTitle, McqQuestionRequest request) {
        Course course = courseRepo.findByTitle(courseTitle)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseTitle));

        try {
            McqQuestion question = new McqQuestion();
            question.setCourse(course);
            question.setQuestion(request.getQuestion());
            question.setOptionA(request.getOptionA());
            question.setOptionB(request.getOptionB());
            question.setOptionD(request.getOptionD());
            question.setCorrectAnswer(request.getCorrectAnswer());
            question.setDifficulty(request.getDifficulty());
            question.setExplanation(request.getExplanation());


            McqQuestion saved = mcqQuestionRepo.save(question);

            return McqQuestionResponse.forAdmin(
                    saved.getId(), course.getTitle(), saved.getQuestion(),
                    saved.getOptionA(), saved.getOptionB(), saved.getOptionC(), saved.getOptionD(),
                    saved.getCorrectAnswer(), saved.getDifficulty().name(), saved.getExplanation());
        }
        catch(Exception e)
        {
            logger.error(e.getLocalizedMessage());
            throw new IllegalArgumentException("Mcq add question error");
        }
    }

    @Transactional
    public McqQuestionResponse updateQuestion(String courseTitle, Integer questionId,
                                              McqQuestionRequest request) {
        McqQuestion question = mcqQuestionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("MCQ question not found: " + questionId));

        try {

            question.setQuestion(request.getQuestion());
            question.setOptionA(request.getOptionA());
            question.setOptionB(request.getOptionB());
            question.setOptionC(request.getOptionC());
            question.setOptionD(request.getOptionD());
            question.setCorrectAnswer(request.getCorrectAnswer());
            question.setDifficulty(request.getDifficulty());
            question.setExplanation(request.getExplanation());

            McqQuestion saved = mcqQuestionRepo.save(question);

            return McqQuestionResponse.forAdmin(
                    saved.getId(), saved.getCourse().getTitle(), saved.getQuestion(),
                    saved.getOptionA(), saved.getOptionB(), saved.getOptionC(), saved.getOptionD(),
                    saved.getCorrectAnswer(), saved.getDifficulty().name(), saved.getExplanation());
        }
        catch(Exception e)
        {
            logger.error(e.getLocalizedMessage());
            throw new IllegalArgumentException("Mcq update question error");
        }
    }


    @Transactional
    public void deleteQuestion(String courseTitle, Integer questionId) {
        try {
            McqQuestion question = mcqQuestionRepo.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("MCQ question not found: " + questionId));
            mcqQuestionRepo.delete(question);
        }
        catch(Exception e)
        {
            logger.error(e.getLocalizedMessage());
            throw new IllegalArgumentException("Mcq delete question error");
        }
    }

    private String calculateNextDifficulty(String currentDifficulty, int score) {
        if (score >= 80) {
            return switch (currentDifficulty) {
                case "EASY" -> "MEDIUM";
                case "MEDIUM" -> "HARD";
                default -> "HARD";
            };
        } else if (score < 50) {
            return switch (currentDifficulty) {
                case "HARD" -> "MEDIUM";
                case "MEDIUM" -> "EASY";
                default -> "EASY";
            };
        }
        return currentDifficulty;
    }

}
