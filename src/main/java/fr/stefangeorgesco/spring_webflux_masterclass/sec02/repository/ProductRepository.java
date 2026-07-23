package fr.stefangeorgesco.spring_webflux_masterclass.sec02.repository;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

    Flux<Product> findByPriceBetween(Integer min, Integer max);
}
