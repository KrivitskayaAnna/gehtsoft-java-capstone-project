package com.gehtsoft.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class CheckAnswersRequestBody {
    private String playerName;
    private List<Answer> answers;

    @Data
    public static class Answer {
        private Long questionId;
        private int answerIdx;
    }
}
