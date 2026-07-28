package fr.stefangeorgesco.spring_webflux_masterclass.sec05.controller;

import fr.stefangeorgesco.spring_webflux_masterclass.sec05.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec05")
@SuppressWarnings({"SpringBootApplicationProperties"})
class CustomerControllerTest {

    private static final String STANDARD_USER_TOKEN = "secret123";
    private static final String PRIME_USER_TOKEN = "secret456";

    @Autowired
    private WebTestClient client;

    @Test
    void unauthorized() {
        // no token
        client.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);

        // invalid token
        validateGet("invalid-token", HttpStatus.UNAUTHORIZED);
    }

    @Test
    void standardCategory() {
        validateGet(STANDARD_USER_TOKEN, HttpStatus.OK);
        validatePost(STANDARD_USER_TOKEN, HttpStatus.FORBIDDEN);
    }

    @Test
    void primeCategory() {
        validateGet(PRIME_USER_TOKEN, HttpStatus.OK);
        validatePost(PRIME_USER_TOKEN, HttpStatus.CREATED);
    }

    /*
        Helper methods
     */

    private void validateGet(String token, HttpStatus expectedStatus) {
        client.get()
                .uri("/customers/3")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void validatePost(String token, HttpStatus expectedStatus) {
        CustomerDto newCustomer = new CustomerDto(null, "new customer", "new.customer@example.com");
        client.post()
                .uri("/customers")
                .bodyValue(newCustomer)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}
