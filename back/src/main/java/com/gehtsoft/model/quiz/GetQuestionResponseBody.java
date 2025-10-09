package com.gehtsoft.model.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetQuestionResponseBody {
    private String question;
    private int questionId;
    private List<String> answers;
}