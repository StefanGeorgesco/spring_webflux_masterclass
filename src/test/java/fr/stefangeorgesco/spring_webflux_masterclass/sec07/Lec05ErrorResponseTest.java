package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.CalculatorResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("LoggingSimilarMessage")
class Lec05ErrorResponseTest extends AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger(Lec05ErrorResponseTest.class);

    private final WebClient client = createWebClient();

    @Test
    void errorHandling_NoError() {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 20, 3)
                .header("operation", "/")
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnError(WebClientResponseException.class, e ->
                        log.error("Error response detail: {}", e.getResponseBodyAs(ProblemDetail.class)))
                .onErrorReturn(WebClientResponseException.BadRequest.class,
                        new CalculatorResponse(0, 0, null, 0.0))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void errorHandling_Error() {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 20, 3)
                .header("operation", "@")
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnError(WebClientResponseException.class, e ->
                        log.error("Error response detail: {}", e.getResponseBodyAs(ProblemDetail.class)))
                .onErrorReturn(WebClientResponseException.BadRequest.class,
                        new CalculatorResponse(0, 0, null, 0.0))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void exchange_NoError() {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 20, 3)
                .header("operation", "/")
                .exchangeToMono(this::decode)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void exchange_Error() {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 20, 3)
                .header("operation", "@")
                .exchangeToMono(this::decode)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    private Mono<CalculatorResponse> decode(ClientResponse clientResponse) {
//        clientResponse.cookies()
//        clientResponse.headers()
        log.info("status code: {}", clientResponse.statusCode());
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ProblemDetail.class)
                    .doOnNext(problemDetail -> log.error("Error response detail: {}", problemDetail))
                    .then(Mono.empty());
        } else {
            return clientResponse.bodyToMono(CalculatorResponse.class);
        }
    }
}
