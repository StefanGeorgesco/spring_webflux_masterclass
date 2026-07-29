package fr.stefangeorgesco.spring_webflux_masterclass.sec06.controller;

import com.jayway.jsonpath.JsonPath;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec06")
@SuppressWarnings({"SpringBootApplicationProperties", "LoggingSimilarMessage"})
class CustomerControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerControllerTest.class);
    private static final String STANDARD_USER_TOKEN = "secret123";
    private static final String PRIME_USER_TOKEN = "secret456";

    @Autowired
    private WebTestClient client;

    @Autowired
    private CustomerRepository customerRepository;

    // happy path tests

    @Test
    void testAllCustomers() {
        client.get()
                .uri("/customers")
                .header("Authorization", "Bearer " + STANDARD_USER_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CustomerDto.class)
                .value(customerDtos -> log.info("Customer list: {}", customerDtos))
                .hasSize(10);
    }

    @Test
    void testAllCustomers_paginated() {
        client.get()
                .uri("/customers/paginated?page=1&size=3&sortBy=name&sortDir=desc")
                .header("Authorization", "Bearer " + STANDARD_USER_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response ->
                        log.info("Response: {}", new String(Objects.requireNonNull(response.getResponseBody()))))
                .jsonPath("$.content.length()").isEqualTo(3)
                .jsonPath("$.content[*].id").isEqualTo(List.of(8, 2, 6))
                .jsonPath("$.content[*].name").isEqualTo(List.of("noah", "mike", "liam"))
                .jsonPath("$.totalElements").isEqualTo(10)
                .jsonPath("$.totalPages").isEqualTo(4)
                .jsonPath("$.number").isEqualTo(1)
                .jsonPath("$.size").isEqualTo(3)
                .jsonPath("$.sort.sorted").isEqualTo(true);
    }

    @Test
    void testGetCustomerById() {
        client.get()
                .uri("/customers/3")
                .header("Authorization", "Bearer " + STANDARD_USER_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response ->
                        log.info("Response: {}", new String(Objects.requireNonNull(response.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.name").isEqualTo("jake")
                .jsonPath("$.email").isEqualTo("jake@gmail.com");
    }

    @Test
    void testCreateAndDeleteCustomer() {
        CustomerDto newCustomer = new CustomerDto(null, "new customer", "new.customer@example.com");

        AtomicInteger id = new AtomicInteger();

        Long count = Objects.requireNonNull(customerRepository.count().block());

        // create customer
        client.post()
                .uri("/customers")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response -> {
                    log.info("Response: {}", new String(Objects.requireNonNull(response.getResponseBody())));
                    id.set(JsonPath.read(new String(Objects.requireNonNull(response.getResponseBody())), "$.id"));
                })
                .jsonPath("$.id").isNumber()
                .jsonPath("$.name").isEqualTo("new customer")
                .jsonPath("$.email").isEqualTo("new.customer@example.com");

        assertEquals(count + 1L, customerRepository.count().block());

        // delete customer
        client.delete()
                .uri("/customers/" + id.get())
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();

        assertEquals(count, customerRepository.count().block());
    }

    @Test
    void testUpdateCustomer() {
        CustomerDto updatedCustomer = new CustomerDto(null, "updated customer",
                "updated.customer@example.com");

        client.put()
                .uri("/customers/1")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response ->
                        log.info("Response: {}", new String(Objects.requireNonNull(response.getResponseBody()))))
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("updated customer")
                .jsonPath("$.email").isEqualTo("updated.customer@example.com");
    }

    // not found error tests

    @Test
    void testGetCustomerById_notFound() {
        client.get()
                .uri("/customers/999")
                .header("Authorization", "Bearer " + STANDARD_USER_TOKEN)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=999] is not found");
    }

    @Test
    void testUpdateCustomer_notFound() {
        CustomerDto updatedCustomer = new CustomerDto(null, "updated customer",
                "updated.customer@example.com");

        client.put()
                .uri("/customers/999")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=999] is not found");
    }

    @Test
    void testDeleteCustomer_notFound() {
        client.delete()
                .uri("/customers/999")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=999] is not found");
    }

    // validation error tests

    @Test
    void testCreateCustomer_noNameValidationError() {
        CustomerDto newCustomer = new CustomerDto(null, null, "new.customer@example.com");

        client.post()
                .uri("/customers")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Name is required");
    }

    @Test
    void testCreateCustomer_blankNameValidationError() {
        CustomerDto newCustomer = new CustomerDto(null, "", "new.customer@example.com");

        client.post()
                .uri("/customers")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Name is required");
    }

    @Test
    void testCreateCustomer_noEmailValidationError() {
        CustomerDto newCustomer = new CustomerDto(null, "new customer", null);

        client.post()
                .uri("/customers")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    @Test
    void testCreateCustomer_blankEmailValidationError() {
        CustomerDto newCustomer = new CustomerDto(null, "new customer", "");

        client.post()
                .uri("/customers")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    @Test
    void testCreateCustomer_invalidEmailValidationError() {
        CustomerDto newCustomer = new CustomerDto(null, "new customer", "invalid-email");

        client.post()
                .uri("/customers")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    @Test
    void testUpdateCustomer_noNameValidationError() {
        CustomerDto updatedCustomer = new CustomerDto(null, null, "email@example.com");

        client.put()
                .uri("/customers/1")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Name is required");
    }

    @Test
    void testUpdateCustomer_blankNameValidationError() {
        CustomerDto updatedCustomer = new CustomerDto(null, "", "email@example.com");

        client.put()
                .uri("/customers/1")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Name is required");
    }

    @Test
    void testUpdateCustomer_noEmailValidationError() {
        CustomerDto updatedCustomer = new CustomerDto(null, "name", null);

        client.put()
                .uri("/customers/1")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    @Test
    void testUpdateCustomer_blankEmailValidationError() {
        CustomerDto updatedCustomer = new CustomerDto(null, "name", "");

        client.put()
                .uri("/customers/1")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    @Test
    void testUpdateCustomer_invalidEmailValidationError() {
        CustomerDto updatedCustomer = new CustomerDto(null, "name", "invalid-email");

        client.put()
                .uri("/customers/1")
                .header("Authorization", "Bearer " + PRIME_USER_TOKEN)
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Valid email is required");
    }

    // authentication and authorization tests

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
        fr.stefangeorgesco.spring_webflux_masterclass.sec05.dto.CustomerDto newCustomer = new fr.stefangeorgesco.spring_webflux_masterclass.sec05.dto.CustomerDto(null, "new customer", "new.customer@example.com");
        client.post()
                .uri("/customers")
                .bodyValue(newCustomer)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}
