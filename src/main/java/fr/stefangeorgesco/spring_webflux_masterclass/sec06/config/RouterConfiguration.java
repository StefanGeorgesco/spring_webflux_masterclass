package fr.stefangeorgesco.spring_webflux_masterclass.sec06.config;

import fr.stefangeorgesco.spring_webflux_masterclass.sec06.exception.CustomerNotFoundException;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.exception.InvalidInputException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfiguration {

    private final CustomerRequestHandler requestHandler;
    private final ApplicationExceptionHandler exceptionHandler;

    public RouterConfiguration(CustomerRequestHandler requestHandler, ApplicationExceptionHandler exceptionHandler) {
        this.requestHandler = requestHandler;
        this.exceptionHandler = exceptionHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()
                .path("customers", this::customerRoutes)
                .onError(InvalidInputException.class, exceptionHandler::handleException)
                .build();
    }

    private RouterFunction<ServerResponse> customerRoutes() {
        return RouterFunctions.route()
                .GET("/paginated", requestHandler::getPaginatedCustomers)
                .GET("/{id}", requestHandler::getCustomerById)
                .GET(requestHandler::getAllCustomers)
                .POST(requestHandler::createCustomer)
                .PUT("/{id}", requestHandler::updateCustomer)
                .DELETE("/{id}", requestHandler::deleteCustomer)
                .onError(CustomerNotFoundException.class, exceptionHandler::handleException)
                .build();
    }
}
