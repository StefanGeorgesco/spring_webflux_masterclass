package fr.stefangeorgesco.spring_webflux_masterclass.sec08.dto;

import java.util.UUID;

public record UploadResponse(UUID confirmationId, Long productsCount) {
}
