package com.gehtsoft.quiz;

import com.gehtsoft.db.QuestionRepository;
import com.gehtsoft.db.model.QuestionDbEntity;
import com.gehtsoft.dto.quiz.GetQuestionResponseBody;
import com.gehtsoft.dto.quiz.OpenDbResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {
    @Autowired
    private QuestionRepository questionRepository;
//
//    @Autowired
//    private ResultRepository resultRepository;

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

//    public CheckAnswersResponseBody checkAnswers(CheckAnswersRequestBody postAnswers)
//            throws SQLException {
//        CheckAnswersResponseBody checkResponse = new CheckAnswersResponseBody(4, List.of(30)); //TODO
//        return ResponseEntity.status(HttpStatus.OK).body(checkResponse);
//    }
}
