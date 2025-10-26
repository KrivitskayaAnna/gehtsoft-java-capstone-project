package com.gehtsoft.unit.controllers;

import com.gehtsoft.dto.leaderboard.GetLeaderboardResponseBody;
import com.gehtsoft.leaderboard.LeaderboardController;
import com.gehtsoft.leaderboard.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerSpec {

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private LeaderboardController leaderboardController;

    @Test
    void returnLeaderboardList() {
        int leadersNum = 3;
        GetLeaderboardResponseBody player1 = new GetLeaderboardResponseBody("Ann1", 10, 1);
        GetLeaderboardResponseBody player2 = new GetLeaderboardResponseBody("Ann2", 9, 2);
        GetLeaderboardResponseBody player3 = new GetLeaderboardResponseBody("Ann3", 3, 3);
        List<GetLeaderboardResponseBody> expectedLeaderboard = Arrays.asList(player1, player2, player3);
        when(leaderboardService.getTopLeaderboard(leadersNum)).thenReturn(expectedLeaderboard);

        ResponseEntity<List<GetLeaderboardResponseBody>> response = leaderboardController.getLeaderboard(leadersNum);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedLeaderboard);
        verify(leaderboardService).getTopLeaderboard(leadersNum);
    }

    @Test
    void returnLeaderboardPlayer() {
        String playerName = "Ann1";
        GetLeaderboardResponseBody player1 = new GetLeaderboardResponseBody("Ann1", 10, 1);
        when(leaderboardService.getByPlayerName(playerName)).thenReturn(Optional.of(player1));

        ResponseEntity<GetLeaderboardResponseBody> response = leaderboardController.getLeaderboardByPlayer(playerName);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(player1);
        verify(leaderboardService).getByPlayerName(playerName);
    }

    @Test
    void returnNotFoundForNonexistingPlayer() {
        String playerName = "Ann4";
        when(leaderboardService.getByPlayerName(playerName)).thenReturn(Optional.empty());

        ResponseEntity<GetLeaderboardResponseBody> response = leaderboardController.getLeaderboardByPlayer(playerName);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void returnEmptyLeaderboardListIfRequestedZero() {
        int leadersNum = 0;
        List<GetLeaderboardResponseBody> emptyList = List.of();
        when(leaderboardService.getTopLeaderboard(leadersNum)).thenReturn(emptyList);

        ResponseEntity<List<GetLeaderboardResponseBody>> response = leaderboardController.getLeaderboard(leadersNum);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}