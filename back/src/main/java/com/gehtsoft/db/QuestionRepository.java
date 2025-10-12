package com.gehtsoft.db;

import com.gehtsoft.db.model.QuestionDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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
                    rs.getLong("question_id"),
                    rs.getString("question"),
                    rs.getInt("correct_answer_score"),
                    rs.getInt("correct_answer_idx")
            );
        }
    }

    public long save(QuestionDbEntity question) {
        String sql = "INSERT INTO quiz.question (question, correct_answer_score, correct_answer_idx) VALUES (:question, :correctAnswerScore, :correctAnswerIdx)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new BeanPropertySqlParameterSource(question);
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"question_id"});
        return keyHolder.getKey().longValue();
    }

    public List<QuestionDbEntity> getByIds(List<Long> questionIds) {
        String sql = "SELECT * FROM quiz.question WHERE question_id IN (:questionIds)";
        Map<String, Object> params = Collections.singletonMap("questionIds", questionIds);
        return jdbcTemplate.query(sql, params, new QuestionRowMapper());
    }
}
