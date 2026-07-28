package fr.stefangeorgesco.spring_webflux_masterclass.sec05.controller;

import fr.stefangeorgesco.spring_webflux_masterclass.sec05.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec05.exception.ApplicationExceptions;
import fr.stefangeorgesco.spring_webflux_masterclass.sec05.service.CustomerService;
import fr.stefangeorgesco.spring_webflux_masterclass.sec05.validator.RequestValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping("paginated")
    public Mono<Page<CustomerDto>> getAllCustomers(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "5") int size,
                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                   @RequestParam(defaultValue = "asc") String sortDir) {
        return customerService.getAllCustomers(
                PageRequest.of(
                        page,
                        size,
                        sortDir.equalsIgnoreCase("desc") ?
                                Sort.by(sortBy).descending() :
                                Sort.by(sortBy).ascending()
                )
        );
    }

    @GetMapping("{id}")
    public Mono<CustomerDto> getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id));
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerDto>> createCustomer(@RequestBody Mono<CustomerDto> customerDtoMono) {
        return customerDtoMono.transform(RequestValidator.validate())
                .as(customerService::createCustomer)
                .map(customerDto ->
                        ResponseEntity.created(URI.create("/customers/" + customerDto.id())).body(customerDto));
    }

    @PutMapping("{id}")
    public Mono<CustomerDto> updateCustomer(@PathVariable Integer id,
                                            @RequestBody Mono<CustomerDto> customerDtoMono) {
        return customerDtoMono.transform(RequestValidator.validate())
                .as(validatedCustomerDtoMono ->
                        customerService.updateCustomer(id, validatedCustomerDtoMono))
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id));
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteCustomer(@PathVariable Integer id) {
        return customerService.deleteCustomer(id)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .then();
    }
}
