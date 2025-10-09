package com.gehtsoft.model.quiz;

import lombok.Data;

import java.util.List;

@Data
public class CheckAnswersRequestBody {
    private String playerName;
    private List<Answer> answers;

    @Data
    private static class Answer {
        private int questionId;
        private int answerIdx;
    }
}
