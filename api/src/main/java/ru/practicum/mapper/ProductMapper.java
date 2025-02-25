package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.entity.Product;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductShortResponse toShortDto(Product product);
    ProductFullResponse toFullDto(Product product);
}
