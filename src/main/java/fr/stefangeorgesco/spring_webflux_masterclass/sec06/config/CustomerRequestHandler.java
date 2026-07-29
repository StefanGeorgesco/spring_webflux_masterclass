package fr.stefangeorgesco.spring_webflux_masterclass.sec06.config;

import fr.stefangeorgesco.spring_webflux_masterclass.sec06.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.exception.ApplicationExceptions;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.service.CustomerService;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.validator.RequestValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class CustomerRequestHandler {

    private final CustomerService customerService;

    public CustomerRequestHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Mono<ServerResponse> getAllCustomers(ServerRequest ignoredRequest) {
        return customerService.getAllCustomers()
                .as(customerDtoFlux ->
                        ServerResponse.ok().body(customerDtoFlux, CustomerDto.class));
    }

    public Mono<ServerResponse> getPaginatedCustomers(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(5);
        String sortBy = request.queryParam("sortBy").orElse("id");
        String sortDir = request.queryParam("sortDir").orElse("asc");
        var pageRequest = PageRequest.of(
                page,
                size,
                sortDir.equalsIgnoreCase("desc") ?
                        Sort.by(sortBy).descending() :
                        Sort.by(sortBy).ascending()
        );
        return customerService.getAllCustomers(pageRequest).flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return customerService.getCustomerById(id)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerDto.class)
                .transform(RequestValidator.validate())
                .as(customerService::createCustomer)
                .flatMap(createdCustomer ->
                        ServerResponse.created(request.uriBuilder().path("/{id}").build(createdCustomer.id()))
                                .bodyValue(createdCustomer));
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return request.bodyToMono(CustomerDto.class)
                .transform(RequestValidator.validate())
                .as(customerDtoMono -> customerService.updateCustomer(id, customerDtoMono))
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return customerService.deleteCustomer(id)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .then(ServerResponse.ok().build());
    }
}
