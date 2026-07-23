package fr.stefangeorgesco.spring_webflux_masterclass.sec02;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.dto.OrderDetails;
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

    @Test
    void testGetOrderDetailsByProductDescription() {
        this.customerOrderRepository.getOrderDetailsByProductDescription("iphone 18")
                .doOnNext(orderDetails -> log.info("Order details for iphone 18: {}", orderDetails))
                .map(OrderDetails::amount)
                .as(StepVerifier::create)
                .expectNext(850, 775, 750)
                .verifyComplete();
    }
}
