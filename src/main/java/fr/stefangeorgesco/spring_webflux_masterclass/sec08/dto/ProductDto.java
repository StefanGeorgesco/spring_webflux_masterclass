package fr.stefangeorgesco.spring_webflux_masterclass.sec08.dto;

public record ProductDto(Integer id,
                         String description,
                         Integer price) {
}
