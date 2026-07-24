package fr.stefangeorgesco.spring_webflux_masterclass.sec03.repository;

import fr.stefangeorgesco.spring_webflux_masterclass.sec03.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

}
