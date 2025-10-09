package com.gehtsoft.quiz;

import com.gehtsoft.model.quiz.CheckAnswersRequestBody;
import com.gehtsoft.model.quiz.CheckAnswersResponseBody;
import com.gehtsoft.model.quiz.GetQuestionResponseBody;
import com.gehtsoft.model.quiz.QuestionLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
//    @Autowired
//    private QuizService quizService;

    @GetMapping
    public ResponseEntity<List<GetQuestionResponseBody>> getQuestions(@RequestParam("questionsLevel") QuestionLevel questionsLevel, @RequestParam("questionsNum") int questionsNum) throws SQLException {
        List<GetQuestionResponseBody> questions = List.of(new GetQuestionResponseBody("What is your age?", 29, List.of("24", "20", "40"))); //TODO
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAnswersResponseBody> checkAnswers(@RequestBody CheckAnswersRequestBody postAnswers) throws SQLException {
        CheckAnswersResponseBody checkResponse = new CheckAnswersResponseBody(4, List.of(30)); //TODO
        return ResponseEntity.status(HttpStatus.OK).body(checkResponse);
    }
}
