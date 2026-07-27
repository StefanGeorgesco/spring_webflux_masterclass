package fr.stefangeorgesco.spring_webflux_masterclass.sec03.controller;

import com.jayway.jsonpath.JsonPath;
import fr.stefangeorgesco.spring_webflux_masterclass.sec03.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec03.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "section=sec03")
@SuppressWarnings({"SpringBootApplicationProperties", "LoggingSimilarMessage"})
class CustomerControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerControllerTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testAllCustomers() {
        client.get()
                .uri("/customers")
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

    @Test
    void testGetCustomerById_notFound() {
        client.get()
                .uri("/customers/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void testUpdateCustomer_notFound() {
        CustomerDto updatedCustomer = new CustomerDto(null, "updated customer",
                "updated.customer@example.com");

        client.put()
                .uri("/customers/999")
                .bodyValue(updatedCustomer)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void testDeleteCustomer_notFound() {
        client.delete()
                .uri("/customers/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}
