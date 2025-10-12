package com.gehtsoft.quiz;

import com.gehtsoft.db.QuestionRepository;
import com.gehtsoft.db.ResultRepository;
import com.gehtsoft.db.model.QuestionDbEntity;
import com.gehtsoft.db.model.ResultDbEntity;
import com.gehtsoft.dto.quiz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuizService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private OpenDbClient openDbClient;

    public Mono<List<GetQuestionResponseBody>> getQuestions(
            QuestionLevel questionsLevel,
            int questionsNum
    ) {
        //TODO: batch insert questions to db
        Mono<OpenDbResponseBody> response = openDbClient.getQuestions(questionsLevel, questionsNum);
        Mono<List<GetQuestionResponseBody>> questions =
                response.map(p -> p.getResults().stream().map(q -> {
                            List<Map.Entry<Integer, String>> mixedAnswers = QuestionDbEntity.randomizeCorrectAnswer(q);
                            int correctAnswerIdx = mixedAnswers.stream().map(Map.Entry::getKey).toList().indexOf(0);
                            long questionIdx = questionRepository.save(new QuestionDbEntity(q.getQuestion(), correctAnswerIdx, questionsLevel));
                            List<String> answers = mixedAnswers.stream().map(Map.Entry::getValue).toList();
                            return new GetQuestionResponseBody(q.getQuestion(), questionIdx, answers);
                        }
                ).toList());
        return questions;
    }

    public CheckAnswersResponseBody checkAnswers(CheckAnswersRequestBody postAnswers) {
        List<Long> questionIds = postAnswers.getAnswers().stream()
                .map(CheckAnswersRequestBody.Answer::getQuestionId).toList();
        Map<Long, QuestionDbEntity> dbQuestions = questionRepository.getByIds(questionIds)
                .stream()
                .collect(Collectors.toMap(QuestionDbEntity::getQuestionId, Function.identity()));
        int maxScore = dbQuestions.values()
                .stream()
                .mapToInt(QuestionDbEntity::getCorrectAnswerScore)
                .sum();
        List<CheckAnswersRequestBody.Answer> correctAnswers = postAnswers.getAnswers()
                .stream()
                .filter(p -> {
                    QuestionDbEntity entity = dbQuestions.get(p.getQuestionId());
                    return entity != null && p.getAnswerIdx() == entity.getCorrectAnswerIdx();
                }).toList();
        int totalScore = correctAnswers
                .stream()
                .mapToInt(p -> dbQuestions.get(p.getQuestionId()).getCorrectAnswerScore())
                .sum();
        List<Long> correctAnswerIds = correctAnswers
                .stream()
                .map(CheckAnswersRequestBody.Answer::getQuestionId)
                .toList();
        resultRepository.save(new ResultDbEntity(postAnswers.getPlayerName(), totalScore));
        return new CheckAnswersResponseBody(maxScore, totalScore, correctAnswerIds);
    }
}
