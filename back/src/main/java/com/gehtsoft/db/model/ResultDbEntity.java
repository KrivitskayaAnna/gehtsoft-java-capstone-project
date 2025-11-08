package com.gehtsoft.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDbEntity {
    private int leaderBoardRank;
    private String playerName;
    private long resultScore;

    public ResultDbEntity(String playerName, long resultScore) {
        this.playerName = playerName;
        this.resultScore = resultScore;
    }
}