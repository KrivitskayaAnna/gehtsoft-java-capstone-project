package com.gehtsoft.db;

import com.gehtsoft.db.model.QuestionDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
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
                    rs.getInt("id"),
                    rs.getInt("question_id"),
                    rs.getInt("correct_answer_score"),
                    rs.getInt("correct_answer_idx")
            );
        }
    }

    public List<QuestionDbEntity> save(List<QuestionDbEntity> questions) {
        String sql = "INSERT INTO quiz.question (question_id, correct_answer_score, correct_answer_idx) VALUES (?, ?, ?)";
        List<Integer> generatedIds = new ArrayList<>();
        jdbcTemplate.getJdbcTemplate().execute((Connection connection) -> {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (QuestionDbEntity question : questions) {
                    ps.setInt(1, question.getQuestionId());
                    ps.setInt(2, question.getCorrectAnswerScore());
                    ps.setInt(3, question.getCorrectAnswerIdx());
                    ps.addBatch();
                }
                ps.executeBatch();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    while (rs.next()) {
                        generatedIds.add(rs.getInt(1));
                    }
                }
                return null;
            }
        });
        List<QuestionDbEntity> savedQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            QuestionDbEntity original = questions.get(i);
            QuestionDbEntity saved = new QuestionDbEntity(
                    original.getQuestionId(),
                    original.getCorrectAnswerIdx(),
                    original.getCorrectAnswerScore()
            );
            saved.setId(generatedIds.get(i));
            savedQuestions.add(saved);
        }
        return savedQuestions;
    }

    public List<QuestionDbEntity> getByIds(List<Integer> questionIds) {
        String sql = "SELECT * FROM quiz.question WHERE id IN (:questionIds)";
        Map<String, Object> params = Collections.singletonMap("questionIds", questionIds);
        return jdbcTemplate.query(sql, params, new QuestionRowMapper());
    }
}
