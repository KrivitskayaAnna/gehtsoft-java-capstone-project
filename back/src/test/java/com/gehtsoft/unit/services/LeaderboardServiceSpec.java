package com.gehtsoft.unit.services;


import com.gehtsoft.db.ResultRepository;
import com.gehtsoft.db.model.ResultDbEntity;
import com.gehtsoft.dto.leaderboard.GetLeaderboardResponseBody;
import com.gehtsoft.leaderboard.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceSpec {
    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void returnEmptyIfNoPlayers() {
        int leadersNum = 5;
        List<ResultDbEntity> data = Collections.emptyList();
        when(resultRepository.getTopPlayers(leadersNum)).thenReturn(data);

        List<GetLeaderboardResponseBody> leaders = leaderboardService.getTopLeaderboard(leadersNum);

        assertThat(leaders).isEmpty();
        verify(resultRepository).getTopPlayers(leadersNum);
    }

    @Test
    void returnTopPlayers() {
        int leadersNum = 2;
        List<ResultDbEntity> data = List.of(
                new ResultDbEntity(2, "Ann1", 2),
                new ResultDbEntity(1, "Ann2", 4)
        );
        when(resultRepository.getTopPlayers(leadersNum)).thenReturn(data);

        List<GetLeaderboardResponseBody> leaders = leaderboardService.getTopLeaderboard(leadersNum);
        List<GetLeaderboardResponseBody> expected = List.of(
                new GetLeaderboardResponseBody("Ann1", 2, 2),
                new GetLeaderboardResponseBody("Ann2", 4, 1)
        );

        assertThat(leaders).isEqualTo(expected);
        verify(resultRepository).getTopPlayers(leadersNum);
    }

    @Test
    void returnAllIfFewPlayers() {
        int leadersNum = 3;
        List<ResultDbEntity> data = List.of(
                new ResultDbEntity(2, "Ann1", 2),
                new ResultDbEntity(1, "Ann2", 4)
        );
        when(resultRepository.getTopPlayers(leadersNum)).thenReturn(data);

        List<GetLeaderboardResponseBody> leaders = leaderboardService.getTopLeaderboard(leadersNum);
        List<GetLeaderboardResponseBody> expected = List.of(
                new GetLeaderboardResponseBody("Ann1", 2, 2),
                new GetLeaderboardResponseBody("Ann2", 4, 1)
        );

        assertThat(leaders).isEqualTo(expected);
        verify(resultRepository).getTopPlayers(leadersNum);
    }

    @Test
    void returnEmptyIfNoSuchPlayer() {
        String playerName = "Ann";
        when(resultRepository.getByPlayer(playerName)).thenReturn(Optional.empty());

        Optional<GetLeaderboardResponseBody> leader = leaderboardService.getByPlayerName(playerName);

        assertThat(leader).isEmpty();
        verify(resultRepository).getByPlayer(playerName);
    }

    @Test
    void returnPlayerIfExists() {
        String playerName = "Ann";
        ResultDbEntity player = new ResultDbEntity(100, "Ann100", 2);
        when(resultRepository.getByPlayer(playerName)).thenReturn(Optional.of(player));

        Optional<GetLeaderboardResponseBody> leader = leaderboardService.getByPlayerName(playerName);
        GetLeaderboardResponseBody expected = new GetLeaderboardResponseBody("Ann100", 2, 100);

        assertThat(leader).isEqualTo(Optional.of(expected));
        verify(resultRepository).getByPlayer(playerName);
    }
}
