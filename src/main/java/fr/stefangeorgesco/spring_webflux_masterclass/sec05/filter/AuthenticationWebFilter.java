package fr.stefangeorgesco.spring_webflux_masterclass.sec05.filter;

import org.jspecify.annotations.NullMarked;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static fr.stefangeorgesco.spring_webflux_masterclass.sec05.filter.WebFilterUtil.getCategory;

@Service
@Order(1)
@NullMarked
public class AuthenticationWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var category = getCategory(exchange);
        if (Objects.nonNull(category)) {
            exchange.getAttributes().put("category", category);
            return chain.filter(exchange);
        }
        return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
    }
}
