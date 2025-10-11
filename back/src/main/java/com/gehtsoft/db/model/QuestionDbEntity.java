package com.gehtsoft.db.model;

import com.gehtsoft.dto.quiz.OpenDbResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDbEntity {
    private long questionId;
    private String question;
    private int correctAnswerScore;
    private int correctAnswerIdx;

    public QuestionDbEntity(String question, int correctAnswerIdx, QuestionLevel questionsLevel) {
        this.question = question;
        this.correctAnswerScore = questionsLevel.getScore();
        this.correctAnswerIdx = correctAnswerIdx;
    }

    public static List<Map.Entry<Integer, String>> randomizeCorrectAnswer(OpenDbResponseBody.QuestionWithVariants question) {
        List<String> answers = question.getIncorrect_answers();
        answers.addFirst(question.getCorrect_answer());
        List<Map.Entry<Integer, String>> idxWithAnswer = IntStream.range(0, answers.size())
                .mapToObj(i -> Map.entry(i, answers.get(i)))
                .collect(Collectors.toList());
        Collections.shuffle(idxWithAnswer);
        return idxWithAnswer;
    }
}