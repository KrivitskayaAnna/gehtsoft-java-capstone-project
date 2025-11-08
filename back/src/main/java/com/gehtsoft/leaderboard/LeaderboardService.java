package com.gehtsoft.leaderboard;

import com.gehtsoft.db.ResultRepository;
import com.gehtsoft.db.model.ResultDbEntity;
import com.gehtsoft.dto.leaderboard.GetLeaderboardResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaderboardService {
    @Autowired
    private ResultRepository resultRepository;

    public List<GetLeaderboardResponseBody> getTopLeaderboard(int leadersNum) {
        List<ResultDbEntity> topPlayers = resultRepository.getTopPlayers(leadersNum);
        return topPlayers.stream().map(p ->
                new GetLeaderboardResponseBody(
                        p.getPlayerName(),
                        p.getResultScore(),
                        p.getLeaderBoardRank()
                )).toList();
    }

    public Optional<GetLeaderboardResponseBody> getByPlayerName(String playerName) {
        Optional<ResultDbEntity> playerEntity = resultRepository.getByPlayer(playerName);
        return playerEntity.map(p ->
                new GetLeaderboardResponseBody(
                        p.getPlayerName(),
                        p.getResultScore(),
                        p.getLeaderBoardRank()
                )
        );
    }
}
