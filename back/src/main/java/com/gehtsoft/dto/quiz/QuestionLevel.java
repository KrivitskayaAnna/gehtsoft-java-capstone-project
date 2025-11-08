package com.gehtsoft.dto.quiz;

public enum QuestionLevel {
    easy(1),
    medium(2),
    hard(3);

    private final int score;

    QuestionLevel(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}