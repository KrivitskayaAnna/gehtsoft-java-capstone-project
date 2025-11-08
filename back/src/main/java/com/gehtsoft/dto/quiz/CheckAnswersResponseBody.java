package com.gehtsoft.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CheckAnswersResponseBody {
    private int maxScore;
    private int totalScore;
    private List<Integer> correctQuestionIds;
}
