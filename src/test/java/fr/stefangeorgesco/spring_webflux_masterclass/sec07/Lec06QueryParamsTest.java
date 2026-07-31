package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.CalculatorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.Map;

class Lec06QueryParamsTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    @Test
    void uriBuilderVariables() {
        var path = "/lec06/calculator";
        var queryParams = "first={first}&second={second}&operation={operation}";

        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .query(queryParams)
                        .build(10, 20, "+"))
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void uriBuilderMap() {
        var path = "/lec06/calculator";
        var queryParams = "first={first}&second={second}&operation={operation}";
        var queryParamsMap = Map.of(
                "first", 10,
                "second", 20,
                "operation", "+"
        );
        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .query(queryParams)
                        .build(queryParamsMap))
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
