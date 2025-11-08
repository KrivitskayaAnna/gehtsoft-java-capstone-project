package com.gehtsoft.db;

import com.gehtsoft.db.model.DataDbEntity;
import com.gehtsoft.dto.quiz.QuestionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DataRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    class DataRowMapper implements RowMapper<DataDbEntity> {
        @Override
        public DataDbEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            Array sqlArray = rs.getArray("incorrect_answers");
            ArrayList<String> incorrectAnswers;
            if (sqlArray != null) {
                incorrectAnswers = new ArrayList<>(Arrays.asList((String[]) sqlArray.getArray()));
            } else incorrectAnswers = new ArrayList<>();
            return new DataDbEntity(
                    rs.getInt("id"),
                    rs.getString("question"),
                    rs.getString("difficulty"),
                    rs.getString("correct_answer"),
                    incorrectAnswers
            );
        }
    }

    public List<DataDbEntity> getRandomN(int questionsNum, QuestionLevel level) {
        String sql = "SELECT * FROM quiz.data WHERE difficulty = :level ORDER BY random() LIMIT (:questionsNum)";
        Map<String, Object> params = new HashMap<>();
        params.put("level", level.toString());
        params.put("questionsNum", questionsNum);
        return jdbcTemplate.query(sql, params, new DataRowMapper());
    }
}
