package com.gehtsoft.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetLeaderboardResponseBody {
    private String playerName;
    private int totalScore;
    private int leaderBoardPlace;
}