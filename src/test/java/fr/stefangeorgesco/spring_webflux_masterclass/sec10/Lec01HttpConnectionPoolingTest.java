package fr.stefangeorgesco.spring_webflux_masterclass.sec10;

import fr.stefangeorgesco.spring_webflux_masterclass.sec10.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Lec01HttpConnectionPoolingTest extends AbstractWebClient {

    private static final int MAX_CONNECTIONS = 501;

    /*
        This is for demo purposes! You might NOT need to adjust all these!
        Default WebClient pool size is 500 connections.
        If the response time is 100 ms => 500 / (100 ms) ==> 5000 req / sec.
     */
    private final WebClient client = createWebClient(builder -> {
        var poolSize = MAX_CONNECTIONS;
        var provider = ConnectionProvider.builder("custom")
                .lifo()
                .maxConnections(poolSize)
                .pendingAcquireMaxCount(poolSize * 5)
                .build();
        var httpClient = HttpClient.create(provider)
                .compress(true)
                .keepAlive(true);
        builder.clientConnector(new ReactorClientHttpConnector(httpClient));
    });

    @Test
    void concurrentRequests() {
        var max = MAX_CONNECTIONS;
        Flux.range(1, max)
                .flatMap(this::getProduct, max)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(products -> assertEquals(max, products.size()))
                .verifyComplete();
    }

    private Mono<Product> getProduct(Integer id) {
        return client.get()
                .uri("/product/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }
}
