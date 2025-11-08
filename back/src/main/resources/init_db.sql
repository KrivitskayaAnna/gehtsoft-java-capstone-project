CREATE SCHEMA IF NOT EXISTS quiz;

CREATE OR REPLACE FUNCTION load_questions_from_json()
RETURNS void AS $$
DECLARE
    json_data JSON;
    question_item JSON;
    incorrect_answers_array TEXT[];
BEGIN
    json_data := pg_read_file('/data/opentdb_data.json');
    FOR question_item IN SELECT * FROM json_array_elements(json_data)
    LOOP
        SELECT ARRAY(
                SELECT json_array_elements_text(question_item->'incorrect_answers')
        ) INTO incorrect_answers_array;

        INSERT INTO quiz.data(question, difficulty, correct_answer, incorrect_answers)
        VALUES (
            question_item->>'question',
            question_item->>'difficulty',
            question_item->>'correct_answer',
            incorrect_answers_array
        );
    END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS quiz.data(
    id SERIAL NOT NULL PRIMARY KEY,
    question TEXT NOT NULL,
    difficulty TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    incorrect_answers TEXT[] NOT NULL,
    datetime TIMESTAMP DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_difficulty ON quiz.data(difficulty);

SELECT load_questions_from_json();

CREATE TABLE IF NOT EXISTS quiz.question(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    question_id INT REFERENCES quiz.data(id),
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