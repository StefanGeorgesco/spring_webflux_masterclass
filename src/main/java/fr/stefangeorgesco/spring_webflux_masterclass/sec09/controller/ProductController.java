package fr.stefangeorgesco.spring_webflux_masterclass.sec09.controller;

import fr.stefangeorgesco.spring_webflux_masterclass.sec09.dto.ProductDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec09.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ProductDto> saveProduct(@RequestBody Mono<ProductDto> mono) {
        return this.service.saveProduct(mono);
    }

    @GetMapping(value = "/stream/{maxPrice}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDto> productStream(@PathVariable Integer maxPrice) {
        return this.service.productStream()
                .filter(productDto -> productDto.price() <= maxPrice);
    }
}
