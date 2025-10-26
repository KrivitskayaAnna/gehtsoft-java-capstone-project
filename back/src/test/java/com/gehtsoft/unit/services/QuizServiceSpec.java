package com.gehtsoft.unit.services;

import com.gehtsoft.db.DataRepository;
import com.gehtsoft.db.QuestionRepository;
import com.gehtsoft.db.ResultRepository;
import com.gehtsoft.db.model.DataDbEntity;
import com.gehtsoft.db.model.QuestionDbEntity;
import com.gehtsoft.dto.quiz.CheckAnswersRequestBody;
import com.gehtsoft.dto.quiz.CheckAnswersResponseBody;
import com.gehtsoft.dto.quiz.GetQuestionResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import com.gehtsoft.quiz.QuizService;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceSpec {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void randomizesAnswers() {
        ArrayList<String> incorrectAnswers = new ArrayList<>();
        incorrectAnswers.add("second");
        incorrectAnswers.add("third");
        DataDbEntity input = new DataDbEntity(
                1, "Lala", "hard", "first", incorrectAnswers
        );
        List<Map.Entry<Integer, String>> firstRandomization = QuizService.randomizeCorrectAnswer(input);
        List<Map.Entry<Integer, String>> secondRandomization = QuizService.randomizeCorrectAnswer(input);
        List<Map.Entry<Integer, String>> thirdRandomization = QuizService.randomizeCorrectAnswer(input);

        Map.Entry<Integer, String> firstEntry = Map.entry(0, "first");
        Map.Entry<Integer, String> secondEntry = Map.entry(1, "second");
        Map.Entry<Integer, String> thirdEntry = Map.entry(2, "third");
        boolean allIdentical = firstRandomization.equals(secondRandomization) &&
                secondRandomization.equals(thirdRandomization);
        assertThat(allIdentical).isFalse();
        assertThat(firstRandomization).containsExactlyInAnyOrder(firstEntry, secondEntry, thirdEntry);
        assertThat(secondRandomization).containsExactlyInAnyOrder(firstEntry, secondEntry, thirdEntry);
        assertThat(thirdRandomization).containsExactlyInAnyOrder(firstEntry, secondEntry, thirdEntry);
    }

    @Test
    void returnEmptyIfNoQuestions() {
        int questionsNum = 5;
        QuestionLevel questionLevel = QuestionLevel.hard;
        List<DataDbEntity> data = Collections.emptyList();
        when(dataRepository.getRandomN(questionsNum, questionLevel)).thenReturn(data);

        List<GetQuestionResponseBody> questions = quizService.getQuestions(questionLevel, questionsNum);

        assertThat(questions).isEmpty();
        verify(dataRepository).getRandomN(questionsNum, questionLevel);
    }

    @Test
    void returnAllIfLessThanRequired() {
        int questionsNum = 2;
        QuestionLevel questionLevel = QuestionLevel.hard;
        ArrayList<String> incorrectAnswers = new ArrayList<>();
        incorrectAnswers.add("second");
        incorrectAnswers.add("third");
        List<DataDbEntity> data = List.of(
                new DataDbEntity(1, "Lala", "hard", "first",
                        incorrectAnswers
                ));
        when(dataRepository.getRandomN(questionsNum, questionLevel)).thenReturn(data);

        List<GetQuestionResponseBody> questions = quizService.getQuestions(questionLevel, questionsNum);

        assertThat(questions).hasSize(1);
        assertThat(questions.getFirst().getQuestionId()).isEqualTo(1);
        assertThat(questions.getFirst().getQuestion()).isEqualTo("Lala");
        assertThat(questions.getFirst().getAnswers()).hasSameElementsAs(List.of("first", "second", "third"));
        verify(dataRepository).getRandomN(questionsNum, questionLevel);
    }

    @Test
    void returnSeveralQuestions() {
        int questionsNum = 2;
        QuestionLevel questionLevel = QuestionLevel.easy;
        ArrayList<String> incorrectAnswers = new ArrayList<>();
        incorrectAnswers.add("second");
        incorrectAnswers.add("third");
        DataDbEntity question1 = new DataDbEntity(1, "Lala", "hard", "first",
                incorrectAnswers
        );
        DataDbEntity question2 = SerializationUtils.clone(question1);
        question2.setId(2);
        List<DataDbEntity> data = List.of(question1, question2);
        when(dataRepository.getRandomN(questionsNum, questionLevel)).thenReturn(data);

        List<GetQuestionResponseBody> questions = quizService.getQuestions(questionLevel, questionsNum);

        assertThat(questions).hasSize(2);
        assertThat(questions.stream().map(GetQuestionResponseBody::getQuestionId)).hasSameElementsAs(List.of(1L, 2L));
        verify(dataRepository).getRandomN(questionsNum, questionLevel);
    }

    @Test
    void returnZeroScoreForNoAnswers() {
        when(questionRepository.getByIds(Collections.emptyList())).thenReturn(Collections.emptyList());

        CheckAnswersRequestBody answers = new CheckAnswersRequestBody(
                "Ann", Collections.emptyList()
        );
        CheckAnswersResponseBody result = quizService.checkAnswers(answers);

        assertThat(result.getMaxScore()).isEqualTo(0);
        assertThat(result.getTotalScore()).isEqualTo(0);
        assertThat(result.getCorrectQuestionIds()).isEqualTo(Collections.emptyList());
        verify(questionRepository).getByIds(Collections.emptyList());
    }

    @Test
    void checkShouldNotScoreUnknownQuestions() {
        List<Integer> questionIds = List.of(1);
        List<QuestionDbEntity> questions = List.of(
                new QuestionDbEntity(2, 3, 1),
                new QuestionDbEntity(3, 2, 0)
        );
        when(questionRepository.getByIds(questionIds)).thenReturn(questions);

        CheckAnswersRequestBody answers = new CheckAnswersRequestBody(
                "Ann", List.of(new CheckAnswersRequestBody.Answer(1, 1))
        );
        CheckAnswersResponseBody result = quizService.checkAnswers(answers);

        assertThat(result.getMaxScore()).isEqualTo(5);
        assertThat(result.getTotalScore()).isEqualTo(0);
        assertThat(result.getCorrectQuestionIds()).isEqualTo(Collections.emptyList());
        verify(questionRepository).getByIds(questionIds);
    }

    @Test
    void checkShouldSumCorrectScores() {
        List<Integer> questionIds = List.of(1, 2, 3);
        List<QuestionDbEntity> questions = List.of(
                new QuestionDbEntity(1, 1, 2),
                new QuestionDbEntity(2, 3, 1),
                new QuestionDbEntity(3, 2, 0)
        );
        when(questionRepository.getByIds(questionIds)).thenReturn(questions);

        CheckAnswersRequestBody answers = new CheckAnswersRequestBody(
                "Ann", List.of(
                new CheckAnswersRequestBody.Answer(1, 1),
                new CheckAnswersRequestBody.Answer(2, 1),
                new CheckAnswersRequestBody.Answer(3, 0)
        )
        );
        CheckAnswersResponseBody result = quizService.checkAnswers(answers);

        assertThat(result.getMaxScore()).isEqualTo(6);
        assertThat(result.getTotalScore()).isEqualTo(5);
        assertThat(result.getCorrectQuestionIds()).isEqualTo(List.of(2, 3));
        verify(questionRepository).getByIds(questionIds);
    }
}