package com.gehtsoft.db;

import com.gehtsoft.db.model.QuestionDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class QuestionRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    class QuestionRowMapper implements RowMapper<QuestionDbEntity> {
        @Override
        public QuestionDbEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new QuestionDbEntity(
                    rs.getInt("question_id"),
                    rs.getInt("correct_answer_score"),
                    rs.getInt("correct_answer_idx")
            );
        }
    }

    public int[] save(List<QuestionDbEntity> questions) {
        String sql = "INSERT INTO quiz.question (question_id, correct_answer_score, correct_answer_idx) " +
                "VALUES (:questionId, :correctAnswerScore, :correctAnswerIdx)";
        List<SqlParameterSource> batchParams = new ArrayList<>();
        for (QuestionDbEntity question : questions) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("questionId", question.getQuestionId());
            params.addValue("correctAnswerScore", question.getCorrectAnswerScore());
            params.addValue("correctAnswerIdx", question.getCorrectAnswerIdx());
            batchParams.add(params);
        }
        return jdbcTemplate.batchUpdate(sql, batchParams.toArray(new SqlParameterSource[0]));
    }

    public List<QuestionDbEntity> getByIds(List<Integer> questionIds) {
        String sql = "SELECT * FROM quiz.question WHERE question_id IN (:questionIds)";
        Map<String, Object> params = Collections.singletonMap("questionIds", questionIds);
        return jdbcTemplate.query(sql, params, new QuestionRowMapper());
    }
}
