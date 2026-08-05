package fr.stefangeorgesco.spring_webflux_masterclass.sec10;

import fr.stefangeorgesco.spring_webflux_masterclass.sec10.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Lec02Http2Test extends AbstractWebClient {

    private final WebClient client = createWebClient(builder -> {
        var poolSize = 1;
        var provider = ConnectionProvider.builder("custom")
                .lifo()
                .maxConnections(poolSize)
                .build();
        var httpClient = HttpClient.create(provider)
                .protocol(HttpProtocol.H2C)
                .compress(true)
                .keepAlive(true);
        builder.clientConnector(new ReactorClientHttpConnector(httpClient));
    });

    @Test
    void concurrentRequests() {
        var max = 20000;
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
