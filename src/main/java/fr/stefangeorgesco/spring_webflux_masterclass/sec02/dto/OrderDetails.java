package fr.stefangeorgesco.spring_webflux_masterclass.sec02.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderDetails(UUID orderId, String customerName, String productName, Integer amount, Instant orderDate) {
}
