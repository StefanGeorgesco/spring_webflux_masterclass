package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

class Lec01MonoTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    @Test
    void simpleGet() throws InterruptedException {
        client.get()
                .uri("/lec01/product/1")
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .subscribe();

        Thread.sleep(Duration.ofSeconds(2));
    }
}
