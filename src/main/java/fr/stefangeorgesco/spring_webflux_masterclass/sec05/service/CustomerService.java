package fr.stefangeorgesco.spring_webflux_masterclass.sec05.service;

import fr.stefangeorgesco.spring_webflux_masterclass.sec05.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec05.mapper.EntityDtoMapper;
import fr.stefangeorgesco.spring_webflux_masterclass.sec05.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Flux<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .map(EntityDtoMapper::toDto);
    }

    public Mono<Page<CustomerDto>> getAllCustomers(PageRequest pageRequest) {
        return customerRepository.findBy(pageRequest)
                .map(EntityDtoMapper::toDto)
                .collectList()
                .zipWith(this.customerRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2()));
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> createCustomer(Mono<CustomerDto> customerDtoMono) {
        return customerDtoMono
                .map(EntityDtoMapper::toEntity)
                .flatMap(customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, Mono<CustomerDto> customerDtoMono) {
        return customerRepository.findById(id)
                .flatMap(existingCustomer -> customerDtoMono)
                .map(EntityDtoMapper::toEntity)
                .doOnNext(customer -> customer.setId(id))
                .flatMap(customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<Boolean> deleteCustomer(Integer id) {
        return customerRepository.deleteCustomerById(id);
    }
}
