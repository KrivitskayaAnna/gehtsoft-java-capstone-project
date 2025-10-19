package com.gehtsoft.db.model;

import com.gehtsoft.dto.quiz.QuestionLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDbEntity {
    private int questionId;
    private int correctAnswerScore;
    private int correctAnswerIdx;

    public QuestionDbEntity(int questionId, int correctAnswerIdx, QuestionLevel questionsLevel) {
        this.questionId = questionId;
        this.correctAnswerScore = questionsLevel.getScore();
        this.correctAnswerIdx = correctAnswerIdx;
    }

    public static List<Map.Entry<Integer, String>> randomizeCorrectAnswer(DataDbEntity question) {
        ArrayList<String> answers = question.getIncorrectAnswers();
        answers.addFirst(question.getCorrectAnswer());
        List<Map.Entry<Integer, String>> idxWithAnswer = IntStream.range(0, answers.size())
                .mapToObj(i -> Map.entry(i, answers.get(i)))
                .collect(Collectors.toList());
        Collections.shuffle(idxWithAnswer);
        return idxWithAnswer;
    }
}