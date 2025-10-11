package com.gehtsoft.quiz;

import com.gehtsoft.dto.quiz.OpenDbResponseBody;
import com.gehtsoft.dto.quiz.QuestionLevel;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenDbClient {
    private final WebClient webClient;

    public OpenDbClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://opentdb.com/api.php")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public Mono<OpenDbResponseBody> getQuestions(
            QuestionLevel questionsLevel,
            int questionsNum
    ) {
        Mono<OpenDbResponseBody> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("amount", questionsNum)
                        .queryParam("difficulty", questionsLevel.name().toLowerCase())
                        .build()
                )
                .retrieve()
                .bodyToMono(OpenDbResponseBody.class);
        return response;
    }
}