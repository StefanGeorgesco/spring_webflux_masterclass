package fr.stefangeorgesco.spring_webflux_masterclass.sec08;

import fr.stefangeorgesco.spring_webflux_masterclass.sec08.dto.ProductDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec08.dto.UploadResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductClient {

    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    public Mono<UploadResponse> uploadProducts(Flux<ProductDto> flux) {
        return client.post()
                .uri("/products/upload")
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(flux, ProductDto.class)
                .retrieve()
                .bodyToMono(UploadResponse.class);
    }

    public Flux<ProductDto> downloadProducts() {
        return client.get()
                .uri("/products/download")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(ProductDto.class);
    }
}
