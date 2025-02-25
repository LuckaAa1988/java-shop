package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.entity.Product;
import ru.practicum.entity.ProductCart;
import ru.practicum.entity.ProductOrder;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductShortResponse toShortDto(Product product);
    ProductFullResponse toFullDto(Product product);

    default Map<ProductShortResponse, Integer> mapProductCartQuantityListToMap(List<ProductCart> products) {
        if (products == null) {
            return Collections.emptyMap();
        }

        return products.stream()
                .collect(Collectors.toMap(
                        pcq -> toShortDto(pcq.getProduct()),
                        ProductCart::getQuantity
                ));
    }

    default Map<ProductShortResponse, Integer> mapProductOrderQuantityListToMap(List<ProductOrder> products) {
        if (products == null) {
            return Collections.emptyMap();
        }

        return products.stream()
                .collect(Collectors.toMap(
                        pcq -> toShortDto(pcq.getProduct()),
                        ProductOrder::getQuantity
                ));
    }
}
