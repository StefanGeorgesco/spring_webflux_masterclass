package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.Map;

class Lec04HeaderTest extends AbstractWebClient {

    private final WebClient client = createWebClient(builder ->
            builder.defaultHeader("caller-id", "order-service"));

    @Test
    void defaultHeader() {
        client.get()
                .uri("/lec04/product/{id}", 15)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void overrideDefaultHeader() {
        client.get()
                .uri("/lec04/product/{id}", 15)
                .header("caller-id", "payment-service")
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void headersMap() {
        var headersMap = Map.of(
                "caller-id", "payment-service",
                "user-id", "12345"
        );

        client.get()
                .uri("/lec04/product/{id}", 15)
                .headers(httpHeaders -> httpHeaders.setAll(headersMap))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
