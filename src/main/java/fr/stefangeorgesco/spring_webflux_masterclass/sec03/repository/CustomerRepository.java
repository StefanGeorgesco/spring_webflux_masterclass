package fr.stefangeorgesco.spring_webflux_masterclass.sec03.repository;

import fr.stefangeorgesco.spring_webflux_masterclass.sec03.entity.Customer;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    @Modifying
    @Query("DELETE FROM customer WHERE id = :id")
    Mono<Boolean> deleteCustomerById(Integer id);
}
