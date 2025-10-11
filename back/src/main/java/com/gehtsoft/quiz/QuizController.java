package com.gehtsoft.quiz;

import com.gehtsoft.dto.quiz.CheckAnswersRequestBody;
import com.gehtsoft.dto.quiz.CheckAnswersResponseBody;
import com.gehtsoft.dto.quiz.GetQuestionResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping
    public Mono<List<GetQuestionResponseBody>> getQuestions(@RequestParam("questionsLevel") QuestionLevel questionsLevel, @RequestParam("questionsNum") int questionsNum) {
        return quizService.getQuestions(questionsLevel, questionsNum);
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAnswersResponseBody> checkAnswers(@RequestBody CheckAnswersRequestBody postAnswers) throws SQLException {
        CheckAnswersResponseBody checkResponse = new CheckAnswersResponseBody(4, List.of(30)); //TODO
        return ResponseEntity.status(HttpStatus.OK).body(checkResponse);
    }
}
