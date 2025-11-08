package com.gehtsoft.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDbEntity implements Serializable {
    private int id;
    private String question;
    private String difficulty;
    private String correctAnswer;
    private ArrayList<String> incorrectAnswers;
}