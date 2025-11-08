package com.gehtsoft.unit.controllers;

import com.gehtsoft.dto.quiz.CheckAnswersRequestBody;
import com.gehtsoft.dto.quiz.CheckAnswersResponseBody;
import com.gehtsoft.dto.quiz.GetQuestionResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import com.gehtsoft.quiz.QuizController;
import com.gehtsoft.quiz.QuizService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizControllerTests {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController quizController;

    @Test
    void returnSeveralQuestions() {
        QuestionLevel level = QuestionLevel.easy;
        int questionsNum = 2;
        GetQuestionResponseBody question1 =
                new GetQuestionResponseBody("Lala", 1, Arrays.asList("3", "4", "5", "6"));
        GetQuestionResponseBody question2 =
                new GetQuestionResponseBody("Lala2", 2, Arrays.asList("first", "second", "third"));
        List<GetQuestionResponseBody> expectedQuestions = Arrays.asList(question1, question2);

        when(quizService.getQuestions(level, questionsNum)).thenReturn(expectedQuestions);

        ResponseEntity<List<GetQuestionResponseBody>> response = quizController.getQuestions(level, questionsNum);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedQuestions);
        verify(quizService).getQuestions(level, questionsNum);
    }

    @Test
    void returnLotOfQuestions() {
        QuestionLevel level = QuestionLevel.easy;
        int questionsNum = 1_999_999; //out of memory for more
        List<GetQuestionResponseBody> expectedQuestions = createLargeQuestionsOutput(questionsNum);

        when(quizService.getQuestions(level, questionsNum)).thenReturn(expectedQuestions);

        ResponseEntity<List<GetQuestionResponseBody>> response = quizController.getQuestions(level, questionsNum);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedQuestions);
        verify(quizService).getQuestions(level, questionsNum);
    }

    @Test
    void returnZeroQuestions() {
        QuestionLevel level = QuestionLevel.easy;
        int questionsNum = 0;
        List<GetQuestionResponseBody> emptyList = Collections.emptyList();

        when(quizService.getQuestions(level, questionsNum)).thenReturn(emptyList);

        ResponseEntity<List<GetQuestionResponseBody>> response = quizController.getQuestions(level, questionsNum);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(quizService).getQuestions(level, questionsNum);
    }


    @Test
    void returnEmptyListIfNoQuestions() {
        QuestionLevel level = QuestionLevel.hard;
        int questionsNum = 5;
        List<GetQuestionResponseBody> emptyList = Collections.emptyList();

        when(quizService.getQuestions(level, questionsNum)).thenReturn(emptyList);

        ResponseEntity<List<GetQuestionResponseBody>> response = quizController.getQuestions(level, questionsNum);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(quizService).getQuestions(level, questionsNum);
    }

    @Test
    void returnCheckedAnswers() {
        CheckAnswersRequestBody checkAnswersRequest =
                new CheckAnswersRequestBody("Ann", List.of(
                        new CheckAnswersRequestBody.Answer(1, 1)
                ));
        CheckAnswersResponseBody checkAnswersResponse = new CheckAnswersResponseBody(4, 3, List.of(1));

        when(quizService.checkAnswers(checkAnswersRequest)).thenReturn(checkAnswersResponse);

        ResponseEntity<CheckAnswersResponseBody> response = quizController.checkAnswers(checkAnswersRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(checkAnswersResponse);
        verify(quizService).checkAnswers(checkAnswersRequest);
    }

    @Test
    void returnIfNothingToCheck() {
        CheckAnswersRequestBody checkAnswersRequest = new CheckAnswersRequestBody("Ann", List.of());
        CheckAnswersResponseBody checkAnswersResponse =
                new CheckAnswersResponseBody(0, 0, List.of());

        when(quizService.checkAnswers(checkAnswersRequest)).thenReturn(checkAnswersResponse);

        ResponseEntity<CheckAnswersResponseBody> response = quizController.checkAnswers(checkAnswersRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(checkAnswersResponse);
        verify(quizService).checkAnswers(checkAnswersRequest);
    }

    @Test
    void checkLotOfQuestions() {
        int questionCount = 1_999_999; //out of memory for more
        CheckAnswersRequestBody largeRequest = createLargeCheckRequest(questionCount);
        CheckAnswersResponseBody largeResponse = createLargeCheckResponse(questionCount);

        when(quizService.checkAnswers(largeRequest)).thenReturn(largeResponse);

        ResponseEntity<CheckAnswersResponseBody> response = quizController.checkAnswers(largeRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(largeResponse);
        verify(quizService).checkAnswers(largeRequest);
    }

    private List<GetQuestionResponseBody> createLargeQuestionsOutput(int count) {
        List<GetQuestionResponseBody> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            questions.add(
                    new GetQuestionResponseBody("Lala" + i, i, Arrays.asList("3", "4", "5", "6"))
            );
        }
        return questions;
    }

    private CheckAnswersRequestBody createLargeCheckRequest(int count) {
        List<CheckAnswersRequestBody.Answer> answers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            answers.add(new CheckAnswersRequestBody.Answer(i, i));
        }
        return new CheckAnswersRequestBody("Ann", answers);
    }

    private CheckAnswersResponseBody createLargeCheckResponse(int count) {
        List<Integer> correct = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            correct.add(i);
        }
        return new CheckAnswersResponseBody(1999999999, 1999999998, correct);
    }
}