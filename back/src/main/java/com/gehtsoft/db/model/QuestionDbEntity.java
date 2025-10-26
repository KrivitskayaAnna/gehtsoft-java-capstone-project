package com.gehtsoft.db.model;

import com.gehtsoft.dto.quiz.QuestionLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}