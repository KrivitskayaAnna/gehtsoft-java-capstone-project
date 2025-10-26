package com.gehtsoft.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CheckAnswersRequestBody {
    private String playerName;
    private List<Answer> answers;

    @Data
    @AllArgsConstructor
    public static class Answer {
        private int questionId;
        private int answerIdx;
    }
}
