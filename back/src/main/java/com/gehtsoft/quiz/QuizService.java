package com.gehtsoft.quiz;

import com.gehtsoft.db.DataRepository;
import com.gehtsoft.db.QuestionRepository;
import com.gehtsoft.db.ResultRepository;
import com.gehtsoft.db.model.DataDbEntity;
import com.gehtsoft.db.model.QuestionDbEntity;
import com.gehtsoft.db.model.ResultDbEntity;
import com.gehtsoft.dto.quiz.CheckAnswersRequestBody;
import com.gehtsoft.dto.quiz.CheckAnswersResponseBody;
import com.gehtsoft.dto.quiz.GetQuestionResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class QuizService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private DataRepository dataRepository;

    public static List<Map.Entry<Integer, String>> randomizeCorrectAnswer(DataDbEntity question) {
        ArrayList<String> answers = new ArrayList<>();
        answers.addAll(question.getIncorrectAnswers());
        answers.addFirst(question.getCorrectAnswer());
        List<Map.Entry<Integer, String>> idxWithAnswer = IntStream.range(0, answers.size())
                .mapToObj(i -> Map.entry(i, answers.get(i)))
                .collect(Collectors.toList());
        Collections.shuffle(idxWithAnswer);
        return idxWithAnswer;
    }

    public List<GetQuestionResponseBody> getQuestions(
            QuestionLevel questionsLevel,
            int questionsNum
    ) {
        List<DataDbEntity> response = dataRepository.getRandomN(questionsNum, questionsLevel);
        List<QuestionDbEntity> questionsToSave = new ArrayList<>();
        List<List<Map.Entry<Integer, String>>> mixedAnswersList = new ArrayList<>();
        for (DataDbEntity q : response) {
            List<Map.Entry<Integer, String>> mixedAnswers = randomizeCorrectAnswer(q);
            mixedAnswersList.add(mixedAnswers);
            int correctAnswerIdx = mixedAnswers.stream().map(Map.Entry::getKey).toList().indexOf(0);
            questionsToSave.add(new QuestionDbEntity(q.getId(), correctAnswerIdx, questionsLevel));
        }
        List<QuestionDbEntity> savedQuestions = questionRepository.save(questionsToSave);
        List<GetQuestionResponseBody> result = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            DataDbEntity q = response.get(i);
            QuestionDbEntity insertedQuestion = savedQuestions.get(i);
            List<Map.Entry<Integer, String>> mixedAnswers = mixedAnswersList.get(i);
            List<String> answers = mixedAnswers.stream().map(Map.Entry::getValue).toList();
            result.add(new GetQuestionResponseBody(q.getQuestion(), insertedQuestion.getId(), answers));
        }
        return result;
    }

    public CheckAnswersResponseBody checkAnswers(CheckAnswersRequestBody postAnswers) {
        List<Integer> questionIds = postAnswers.getAnswers().stream()
                .map(CheckAnswersRequestBody.Answer::getQuestionId).toList();
        Map<Integer, QuestionDbEntity> dbQuestions = questionRepository.getByIds(questionIds)
                .stream()
                .collect(Collectors.toMap(QuestionDbEntity::getId, Function.identity()));
        int maxScore = dbQuestions.values()
                .stream()
                .mapToInt(QuestionDbEntity::getCorrectAnswerScore)
                .sum();
        List<CheckAnswersRequestBody.Answer> correctAnswers = postAnswers.getAnswers()
                .stream()
                .filter(p -> {
                    QuestionDbEntity entity = dbQuestions.get(p.getQuestionId());
                    return entity != null && p.getAnswerIdx() == entity.getCorrectAnswerIdx();
                }).toList();
        int totalScore = correctAnswers
                .stream()
                .mapToInt(p -> dbQuestions.get(p.getQuestionId()).getCorrectAnswerScore())
                .sum();
        List<Integer> correctAnswerIds = correctAnswers
                .stream()
                .map(CheckAnswersRequestBody.Answer::getQuestionId)
                .toList();
        resultRepository.save(new ResultDbEntity(postAnswers.getPlayerName(), totalScore));
        return new CheckAnswersResponseBody(maxScore, totalScore, correctAnswerIds);
    }
}
