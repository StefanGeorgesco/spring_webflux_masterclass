package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

class Lec03PostTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    @Test
    void postBodyValue() {
        var product = new Product(null, "Product 1", 100);

        client.post()
                .uri("/lec03/product")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void postBody() {
        var productMono = Mono.fromSupplier(() -> new Product(null, "Product 1", 100))
                .delayElement(Duration.ofSeconds(1));

        client.post()
                .uri("/lec03/product")
                .body(productMono, Product.class)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}