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

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuizService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private DataRepository dataRepository;

    public List<GetQuestionResponseBody> getQuestions(
            QuestionLevel questionsLevel,
            int questionsNum
    ) {
        List<DataDbEntity> response = dataRepository.getRandomN(questionsNum, questionsLevel);
        List<AbstractMap.SimpleEntry<QuestionDbEntity, GetQuestionResponseBody>> entities =
                response.stream().map(q -> {
                            List<Map.Entry<Integer, String>> mixedAnswers = QuestionDbEntity.randomizeCorrectAnswer(q);
                            int correctAnswerIdx = mixedAnswers.stream().map(Map.Entry::getKey).toList().indexOf(0);
                            int questionIdx = q.getId();
                            List<String> answers = mixedAnswers.stream().map(Map.Entry::getValue).toList();
                            return new AbstractMap.SimpleEntry<>(
                                    new QuestionDbEntity(questionIdx, correctAnswerIdx, questionsLevel),
                                    new GetQuestionResponseBody(q.getQuestion(), questionIdx, answers)
                            );
                        }
                ).toList();
        List<QuestionDbEntity> questionEntities = entities.stream().map(AbstractMap.SimpleEntry::getKey).toList();
        questionRepository.save(questionEntities);
        return entities.stream().map(AbstractMap.SimpleEntry::getValue).toList();
    }

    public CheckAnswersResponseBody checkAnswers(CheckAnswersRequestBody postAnswers) {
        List<Integer> questionIds = postAnswers.getAnswers().stream()
                .map(CheckAnswersRequestBody.Answer::getQuestionId).toList();
        Map<Integer, QuestionDbEntity> dbQuestions = questionRepository.getByIds(questionIds)
                .stream()
                .collect(Collectors.toMap(QuestionDbEntity::getQuestionId, Function.identity()));
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
