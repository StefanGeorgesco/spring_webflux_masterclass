package fr.stefangeorgesco.spring_webflux_masterclass.sec03.controller;

import fr.stefangeorgesco.spring_webflux_masterclass.sec03.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec03.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Flux<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<CustomerDto>> getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerDto>> createCustomer(@RequestBody Mono<CustomerDto> customerDtoMono) {
        return customerService.createCustomer(customerDtoMono)
                .map(customerDto ->
                        ResponseEntity.created(URI.create("/customers/" + customerDto.id())).body(customerDto));
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<CustomerDto>> updateCustomer(@PathVariable Integer id,
                                                            @RequestBody Mono<CustomerDto> customerDtoMono) {
        return customerService.updateCustomer(id, customerDtoMono)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Integer id) {
        return customerService.deleteCustomer(id)
                .filter(Boolean::booleanValue)
                .map(deleted -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
