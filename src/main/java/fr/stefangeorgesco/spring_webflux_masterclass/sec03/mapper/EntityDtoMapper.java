package fr.stefangeorgesco.spring_webflux_masterclass.sec03.mapper;

import fr.stefangeorgesco.spring_webflux_masterclass.sec03.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec03.entity.Customer;

public class EntityDtoMapper {

    private EntityDtoMapper() {
    }

    public static Customer toEntity(CustomerDto customerDto) {
        var customer = new Customer();
        customer.setId(customerDto.id());
        customer.setName(customerDto.name());
        customer.setEmail(customerDto.email());
        return customer;
    }

    public static CustomerDto toDto(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getName(), customer.getEmail());
    }
}
