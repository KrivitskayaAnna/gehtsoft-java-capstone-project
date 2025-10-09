package com.gehtsoft.model.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CheckAnswersResponseBody {
    private int totalScore;
    private List<Integer> correctQuestionIds;
}
