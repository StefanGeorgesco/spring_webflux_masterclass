package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class Lec07BasicAuthTest extends AbstractWebClient {

    private final WebClient client = createWebClient(builder ->
            builder.defaultHeaders(headers ->
                    headers.setBasicAuth("java", "secret")));

    @Test
    void basicAuth() {
        client.get()
                .uri("/lec07/product/{id}", 15)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
