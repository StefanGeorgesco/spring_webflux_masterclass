package fr.stefangeorgesco.spring_webflux_masterclass.sec06.assignment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

import java.util.function.IntBinaryOperator;

/*
    /calculator/{a}/{b}

    header: operation: +,-,*,/
 */

@Configuration
public class CalculatorAssignment {

    @Bean
    public RouterFunction<ServerResponse> calculator() {
        return RouterFunctions.route()
                .path("calculator", this::calculatorRoutes)
                .build();
    }

    private RouterFunction<ServerResponse> calculatorRoutes() {
        return RouterFunctions.route()
                .GET("/{a}/0", badRequest("b cannot be 0"))
                .GET("/{a}/{b}", operationIs("+"), handleWith(Integer::sum))
                .GET("/{a}/{b}", operationIs("-"), handleWith((a, b) -> a - b))
                .GET("/{a}/{b}", operationIs("*"), handleWith((a, b) -> a * b))
                .GET("/{a}/{b}", operationIs("/"), handleWith((a, b) -> a / b))
                .GET("/{a}/{b}", badRequest("operation header should be one of +, -, *, /"))
                .build();
    }

    private RequestPredicate operationIs(String operation) {
        return RequestPredicates.headers(headers ->
                operation.equals(headers.firstHeader("operation")));
    }

    private HandlerFunction<ServerResponse> handleWith(IntBinaryOperator operator) {
        return request -> {
            int a = Integer.parseInt(request.pathVariable("a"));
            int b = Integer.parseInt(request.pathVariable("b"));
            int result = operator.applyAsInt(a, b);
            return ServerResponse.ok().bodyValue(result);
        };
    }

    private HandlerFunction<ServerResponse> badRequest(String message) {
        return request -> ServerResponse.badRequest().bodyValue(message);
    }
}
