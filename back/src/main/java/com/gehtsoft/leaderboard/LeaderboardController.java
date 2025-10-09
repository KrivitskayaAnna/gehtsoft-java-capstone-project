package com.gehtsoft.leaderboard;

import com.gehtsoft.model.leaderboard.GetLeaderboardResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
//    @Autowired
//    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<GetLeaderboardResponseBody>> getLeaderboard(@RequestParam("leadersNum") int leadersNum) throws SQLException {
        List<GetLeaderboardResponseBody> leaderboard = List.of(new GetLeaderboardResponseBody("Ann", 20, 1)); //TODO
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/{playerName}")
    public ResponseEntity<GetLeaderboardResponseBody> getLeaderboardByPlayer(@PathVariable String playerName) throws SQLException {
        GetLeaderboardResponseBody player = new GetLeaderboardResponseBody("Ann", 20, 1); //TODO
        return ResponseEntity.ok(player);
    }
}
