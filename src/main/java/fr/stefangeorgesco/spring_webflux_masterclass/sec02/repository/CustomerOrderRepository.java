package fr.stefangeorgesco.spring_webflux_masterclass.sec02.repository;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.entity.CustomerOrder;
import fr.stefangeorgesco.spring_webflux_masterclass.sec02.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {

    @Query("""
            SELECT
                p.*
            FROM
                customer c
            INNER JOIN customer_order co ON c.id = co.customer_id
            INNER JOIN product p ON co.product_id = p.id
            WHERE
                c.name = :customerName
            """)
    Flux<Product> getProductsOrderedByCustomer(String customerName);
}
