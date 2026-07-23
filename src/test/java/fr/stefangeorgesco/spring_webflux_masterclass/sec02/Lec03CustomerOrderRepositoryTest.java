package fr.stefangeorgesco.spring_webflux_masterclass.sec02;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.entity.Product;
import fr.stefangeorgesco.spring_webflux_masterclass.sec02.repository.CustomerOrderRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class Lec03CustomerOrderRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec03CustomerOrderRepositoryTest.class);

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Test
    void testGetProductsOrderedByCustomer() {
        this.customerOrderRepository.getProductsOrderedByCustomer("mike")
                .doOnNext(product -> log.info("Product ordered by sam: {}", product))
                .map(Product::getDescription)
                .as(StepVerifier::create)
                .expectNext("iphone 20", "mac pro")
                .verifyComplete();
    }
}
