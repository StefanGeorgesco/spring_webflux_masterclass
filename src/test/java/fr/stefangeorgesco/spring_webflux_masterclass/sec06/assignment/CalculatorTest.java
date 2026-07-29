package fr.stefangeorgesco.spring_webflux_masterclass.sec06.assignment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec06")
@SuppressWarnings("SpringBootApplicationProperties")
class CalculatorTest {

    @Autowired
    private WebTestClient client;

    @Test
    void calculator() {
        // success
        validate(20, 10, "+", 200, "30");
        validate(20, 10, "-", 200, "10");
        validate(20, 10, "*", 200, "200");
        validate(20, 10, "/", 200, "2");

        // bad requests
        validate(20, 0, "+", 400, "b cannot be 0");
        validate(20, 10, "@", 400, "operation header should be one of +, -, *, /");
        validate(20, 10, null, 400, "operation header should be one of +, -, *, /");
    }

    @SuppressWarnings({"SameParameterValue", "UastIncorrectHttpHeaderInspection"})
    private void validate(int a, int b, String operation, int statusCode, String expectedResult) {
        this.client.get()
                .uri("/calculator/{a}/{b}", a, b)
                .headers(h -> {
                    if (Objects.nonNull(operation)) {
                        h.add("operation", operation);
                    }
                })
                .exchange()
                .expectStatus().isEqualTo(statusCode)
                .expectBody(String.class)
                .value(s -> {
                    assertNotNull(s);
                    assertEquals(expectedResult, s);
                });
    }
}
