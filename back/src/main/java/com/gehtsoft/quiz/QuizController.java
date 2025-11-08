package com.gehtsoft.quiz;

import com.gehtsoft.dto.quiz.CheckAnswersRequestBody;
import com.gehtsoft.dto.quiz.CheckAnswersResponseBody;
import com.gehtsoft.dto.quiz.GetQuestionResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping
    public ResponseEntity<List<GetQuestionResponseBody>> getQuestions(@RequestParam("questionsLevel") QuestionLevel questionsLevel, @RequestParam("questionsNum") int questionsNum) {
        return ResponseEntity.ok(quizService.getQuestions(questionsLevel, questionsNum));
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAnswersResponseBody> checkAnswers(@RequestBody CheckAnswersRequestBody postAnswers) {
        return ResponseEntity.ok(quizService.checkAnswers(postAnswers));
    }
}
