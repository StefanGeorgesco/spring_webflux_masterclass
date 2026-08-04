package fr.stefangeorgesco.spring_webflux_masterclass.sec08.repository;

import fr.stefangeorgesco.spring_webflux_masterclass.sec08.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
}
