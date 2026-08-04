package fr.stefangeorgesco.spring_webflux_masterclass.sec07;

import fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.UUID;

class Lec09ExchangeFilterTest extends AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger(Lec09ExchangeFilterTest.class);

    private final WebClient client = createWebClient(builder ->
            builder
                    .filter(tokenGenerator())
                    .filter(logRequest()));

    @Test
    void exchangeFilter() {
        for (int i = 1; i <= 5; i++) {
            client.get()
                    .uri("/lec09/product/{id}", i)
                    .attribute("enable-logging", i % 2 == 1)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .doOnNext(print())
                    .then()
                    .as(StepVerifier::create)
                    .expectComplete()
                    .verify();
        }
    }

    private ExchangeFilterFunction tokenGenerator() {
        return (request, next) -> {
            var token = UUID.randomUUID().toString().replace("-", "");
            log.info("generated token: {}", token);
            var modifiedRequest = ClientRequest.from(request)
                    .headers(headers -> headers.setBearerAuth(token))
                    .build();
            return next.exchange(modifiedRequest);
        };
    }

    private ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            var isEnabled = (Boolean) request.attributes().getOrDefault("enable-logging", false);
            if (isEnabled) {
                log.info("Request: {} {}", request.method(), request.url());
            }
            return next.exchange(request);
        };
    }
}
