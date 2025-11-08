package com.gehtsoft.db;

import com.gehtsoft.db.model.ResultDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ResultRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    class QuestionRowMapper implements RowMapper<ResultDbEntity> {
        @Override
        public ResultDbEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ResultDbEntity(
                    rs.getInt("leader_board_rank"),
                    rs.getString("player_name"),
                    rs.getInt("result_score")
            );
        }
    }

    public void save(ResultDbEntity question) {
        String sql = "INSERT INTO quiz.result (player_name, result_score) " +
                "VALUES (:playerName, :resultScore)";
        Map<String, Object> params = new HashMap<>();
        params.put("playerName", question.getPlayerName());
        params.put("resultScore", question.getResultScore());
        jdbcTemplate.update(sql, params);
    }

    public List<ResultDbEntity> getTopPlayers(int topNum) {
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY sum(result_score) DESC) AS leader_board_rank, player_name, sum(result_score) AS result_score " +
                "FROM quiz.result GROUP BY player_name ORDER BY sum(result_score) DESC LIMIT :topNum";
        Map<String, Object> params = Collections.singletonMap("topNum", topNum);
        return jdbcTemplate.query(sql, params, new QuestionRowMapper());
    }

    public Optional<ResultDbEntity> getByPlayer(String playerName) {
        String sql = "SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY sum(result_score) DESC) AS leader_board_rank, " +
                "player_name, sum(result_score) AS result_score " +
                "FROM quiz.result GROUP BY player_name) tmp WHERE player_name IN (:playerName)";
        Map<String, Object> params = Collections.singletonMap("playerName", playerName);
        List<ResultDbEntity> results = jdbcTemplate.query(sql, params, new QuestionRowMapper());
        return results.stream().findFirst();
    }
}
