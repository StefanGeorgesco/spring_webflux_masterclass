package fr.stefangeorgesco.spring_webflux_masterclass.sec09.service;

import fr.stefangeorgesco.spring_webflux_masterclass.sec09.dto.ProductDto;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@NullMarked
public class DataSetupService implements CommandLineRunner {

    private final ProductService productService;

    public DataSetupService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) {
        Flux.range(1, 10000)
                .delayElements(Duration.ofSeconds(1))
                .map(i ->
                        new ProductDto(null,
                                "product-" + i,
                                ThreadLocalRandom.current().nextInt(1, 100)))
                .flatMap(productDto -> productService.saveProduct(Mono.just(productDto)))
                .subscribe();
    }
}
