package fr.stefangeorgesco.spring_webflux_masterclass.sec09.config;

import fr.stefangeorgesco.spring_webflux_masterclass.sec09.dto.ProductDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class ApplicationConfig {

    @Bean
    public Sinks.Many<ProductDto> productSink() {
        return Sinks.many().replay().limit(1);
    }
}
