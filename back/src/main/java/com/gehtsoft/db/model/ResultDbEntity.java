package com.gehtsoft.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDbEntity {
    private long gameId;
    private String playerName;
    private int resultScore;
}