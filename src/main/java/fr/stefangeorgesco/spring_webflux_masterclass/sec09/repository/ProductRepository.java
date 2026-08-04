package fr.stefangeorgesco.spring_webflux_masterclass.sec09.repository;

import fr.stefangeorgesco.spring_webflux_masterclass.sec09.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
}
