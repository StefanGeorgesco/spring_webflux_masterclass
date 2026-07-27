package fr.stefangeorgesco.spring_webflux_masterclass.sec02;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.entity.Customer;
import fr.stefangeorgesco.spring_webflux_masterclass.sec02.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SuppressWarnings("LoggingSimilarMessage")
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

    @Test
    void testInsertAndDeleteCustomer() {
        // count
        this.customerRepository.count()
                .doOnNext(count -> log.info("countCustomer: {}", count))
                .as(StepVerifier::create)
                .expectNext(10L)
                .verifyComplete();

        AtomicInteger id = new AtomicInteger();

        // insert
        Customer customer = new Customer();
        customer.setName("marshal");
        customer.setEmail("marshal@gmail.com");
        this.customerRepository.save(customer)
                .doOnNext(savedCustomer -> {
                    log.info("insertedCustomer: {}", savedCustomer);
                    id.set(savedCustomer.getId());
                })
                .as(StepVerifier::create)
                .assertNext(savedCustomer -> assertNotNull(savedCustomer.getId()))
                .verifyComplete();

        // count
        this.customerRepository.count()
                .doOnNext(count -> log.info("countCustomer: {}", count))
                .as(StepVerifier::create)
                .expectNext(11L)
                .verifyComplete();

        // delete
        this.customerRepository.deleteById(id.get())
                .then(this.customerRepository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .verifyComplete();
    }

    @Test
    void testUpdateCustomer() {
        this.customerRepository.findByName("ethan")
                .doOnNext(customer -> customer.setName("noel"))
                .flatMap(this.customerRepository::save)
                .doOnNext(updatedCustomer -> log.info("afterUpdateCustomer: {}", updatedCustomer))
                .as(StepVerifier::create)
                .assertNext(updatedCustomer -> assertEquals("noel", updatedCustomer.getName()))
                .verifyComplete();
    }
}
