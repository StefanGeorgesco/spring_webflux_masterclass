package fr.stefangeorgesco.spring_webflux_masterclass.sec08;

import fr.stefangeorgesco.spring_webflux_masterclass.sec08.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;

/*
    Just for demo
 */
class ProductsUploadDownloadTest {

    private static final Logger log = LoggerFactory.getLogger(ProductsUploadDownloadTest.class);

    private final ProductClient productClient = new ProductClient();

    @Test
    void upload() {
        var flux = Flux.range(1, 1_000_000)
                .map(i -> new ProductDto(null, "description-" + i, i * 10));

        this.productClient.uploadProducts(flux)
                .doOnNext(uploadResponse -> log.info("Upload response: {}", uploadResponse))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void download() {
        this.productClient.downloadProducts()
                .map(ProductDto::toString)
                .as(flux -> FileWriter.create(flux, Path.of("products.txt")))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
