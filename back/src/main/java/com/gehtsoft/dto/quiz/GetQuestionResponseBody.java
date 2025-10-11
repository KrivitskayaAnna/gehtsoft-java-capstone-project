package com.gehtsoft.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetQuestionResponseBody {
    private String question;
    private long questionId;
    private List<String> answers;
}