package fr.stefangeorgesco.spring_webflux_masterclass.sec09;

import fr.stefangeorgesco.spring_webflux_masterclass.sec09.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SpringBootApplicationProperties")
@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec09")
class ServerSentEventsTest {

    private static final Logger log = LoggerFactory.getLogger(ServerSentEventsTest.class);

    @Autowired
    private WebTestClient client;

    @Test
    void serverSentEvents() {
        client.get()
                .uri("/products/stream/80")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody()
                .take(3)
                .doOnNext(product -> log.info("Product: {}", product))
                .collectList()
                .as(StepVerifier::create)
                .assertNext(products -> {
                    assertEquals(3, products.size());
                    assertTrue(products.stream().allMatch(product -> product.price() <= 80));
                })
                .verifyComplete();
    }
}
