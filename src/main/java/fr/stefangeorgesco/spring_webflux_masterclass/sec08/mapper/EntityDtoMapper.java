package fr.stefangeorgesco.spring_webflux_masterclass.sec08.mapper;

import fr.stefangeorgesco.spring_webflux_masterclass.sec08.dto.ProductDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec08.entity.Product;

public class EntityDtoMapper {

    private EntityDtoMapper() {
    }

    public static Product toEntity(ProductDto productDto) {
        var product = new Product();
        product.setId(productDto.id());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        return product;
    }

    public static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
