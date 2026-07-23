package fr.stefangeorgesco.spring_webflux_masterclass.sec02;

import fr.stefangeorgesco.spring_webflux_masterclass.sec02.dto.OrderDetails;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.test.StepVerifier;

class Lec04DatabaseClientTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec04DatabaseClientTest.class);

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    @SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
    void testGetOrderDetailsByProductDescription() {
        String query = """
                SELECT
                    co.order_id,
                    c.name AS customer_name,
                    p.description AS product_name,
                    co.amount,
                    co.order_date
                FROM
                    customer c
                INNER JOIN customer_order co ON c.id = co.customer_id
                INNER JOIN product p ON p.id = co.product_id
                WHERE
                    p.description = :productDescription
                ORDER BY co.amount DESC
                """;

        this.databaseClient.sql(query)
                .bind("productDescription", "iphone 18")
                .mapProperties(OrderDetails.class)
                .all()
                .doOnNext(orderDetails -> log.info("Order details for iphone 18: {}", orderDetails))
                .map(OrderDetails::amount)
                .as(StepVerifier::create)
                .expectNext(850, 775, 750)
                .verifyComplete();
    }
}
