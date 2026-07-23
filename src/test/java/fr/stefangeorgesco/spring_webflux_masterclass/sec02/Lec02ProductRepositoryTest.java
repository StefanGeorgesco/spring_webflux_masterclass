package fr.stefangeorgesco.spring_webflux_masterclass.sec02;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.entity.Product;
import fr.stefangeorgesco.spring_webflux_masterclass.sec02.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

class Lec02ProductRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec02ProductRepositoryTest.class);

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFindByPriceBetween() {
        this.productRepository.findByPriceBetween(200, 400)
                .doOnNext(product -> log.info("findByPriceBetweenProduct: {}", product))
                .as(StepVerifier::create)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void testFindByPageable() {
        this.productRepository.findBy(
                        PageRequest.of(1, 3)
                                .withSort(Sort.by("price").ascending())
                )
                .doOnNext(product -> log.info("findByPageableProduct: {}", product))
                .map(Product::getDescription)
                .as(StepVerifier::create)
                .expectNext("apple watch", "iphone 18", "ipad")
                .verifyComplete();
    }
}
