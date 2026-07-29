package fr.stefangeorgesco.spring_webflux_masterclass.sec06.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfiguration {

    private final CustomerRequestHandler customerRequestHandler;

    public RouterConfiguration(CustomerRequestHandler customerRequestHandler) {
        this.customerRequestHandler = customerRequestHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return RouterFunctions.route()
                .GET("customers", customerRequestHandler::getAllCustomers)
                .GET("customers/{id}", customerRequestHandler::getCustomerById)
                .POST("customers", customerRequestHandler::createCustomer)
                .PUT("customers/{id}", customerRequestHandler::updateCustomer)
                .DELETE("customers/{id}", customerRequestHandler::deleteCustomer)
                .build();
    }
}
