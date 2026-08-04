package fr.stefangeorgesco.spring_webflux_masterclass.sec09.dto;

public record ProductDto(Integer id,
                         String description,
                         Integer price) {
}
