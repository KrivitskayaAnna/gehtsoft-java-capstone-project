package com.gehtsoft.leaderboard;

import com.gehtsoft.dto.leaderboard.GetLeaderboardResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<GetLeaderboardResponseBody>> getLeaderboard(
            @RequestParam("leadersNum") int leadersNum
    ) {
        return ResponseEntity.ok(leaderboardService.getTopLeaderboard(leadersNum));
    }

    @GetMapping("/{playerName}")
    public ResponseEntity<GetLeaderboardResponseBody> getLeaderboardByPlayer(
            @PathVariable String playerName
    ) {
        Optional<GetLeaderboardResponseBody> result = leaderboardService.getByPlayerName(playerName);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
