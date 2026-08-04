package fr.stefangeorgesco.spring_webflux_masterclass.sec09.service;

import fr.stefangeorgesco.spring_webflux_masterclass.sec09.dto.ProductDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec09.mapper.EntityDtoMapper;
import fr.stefangeorgesco.spring_webflux_masterclass.sec09.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final Sinks.Many<ProductDto> productSink;

    public ProductService(ProductRepository repository, Sinks.Many<ProductDto> productSink) {
        this.repository = repository;
        this.productSink = productSink;
    }

    public Mono<ProductDto> saveProduct(Mono<ProductDto> mono) {
        return mono.map(EntityDtoMapper::toEntity)
                .flatMap(this.repository::save)
                .map(EntityDtoMapper::toDto)
                .doOnNext(productSink::tryEmitNext);
    }

    public Flux<ProductDto> productStream() {
        return productSink.asFlux();
    }
}
