CREATE SCHEMA IF NOT EXISTS quiz;

CREATE TABLE IF NOT EXISTS quiz.question(
    question_id BIGSERIAL NOT NULL PRIMARY KEY,
    question TEXT NOT NULL,
    correct_answer_score INT NOT NULL,
    correct_answer_idx INT NOT NULL,
    datetime TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quiz.result(
    game_id BIGSERIAL NOT NULL PRIMARY KEY,
    player_name TEXT NOT NULL,
    result_score INT NOT NULL,
    datetime TIMESTAMP DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_result_player ON quiz.result(player_name);