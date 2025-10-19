package com.gehtsoft.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenDbResponseBody {
    private List<QuestionWithVariants> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionWithVariants {
        private String question;
        private String correct_answer;
        private List<String> incorrect_answers;
    }
}