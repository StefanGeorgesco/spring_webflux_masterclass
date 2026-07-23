package fr.stefangeorgesco.spring_webflux_masterclass.sec02;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;


class Lec01CustomerRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindAll() {
        this.customerRepository.findAll()
                .doOnNext(customer -> log.info("findAllCustomer: {}", customer))
                .as(StepVerifier::create)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        this.customerRepository.findById(2)
                .doOnNext(customer -> log.info("findByIdCustomer: {}", customer))
                .as(StepVerifier::create)
                .assertNext(customer -> assertEquals("mike", customer.getName()))
                .verifyComplete();
    }

    @Test
    void testFindByName() {
        this.customerRepository.findByName("jake")
                .doOnNext(customer -> log.info("findByNameCustomer: {}", customer))
                .as(StepVerifier::create)
                .assertNext(customer -> assertEquals("jake@gmail.com", customer.getEmail()))
                .verifyComplete();
    }

    @Test
    void testFindByEmailEndingWith() {
        this.customerRepository.findByEmailEndingWith("ke@gmail.com")
                .doOnNext(customer -> log.info("findByEmailEndingWithCustomer: {}", customer))
                .as(StepVerifier::create)
                .assertNext(customer -> assertEquals("mike", customer.getName()))
                .assertNext(customer -> assertEquals("jake", customer.getName()))
                .verifyComplete();
    }
}
