package ru.practicum.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.entity.Order;
import ru.practicum.entity.ProductCart;
import ru.practicum.entity.ProductOrder;
import ru.practicum.response.OrderFullResponse;

import java.util.List;


@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderMapper {

    @Mapping(target = "products", expression = "java(productMapper.mapProductOrderQuantityListToMap(order.getProducts()))")
    @Mapping(target = "id", source = "orderId")
    OrderFullResponse toDto(Order order, @Context ProductMapper productMapper);

    ProductOrder toOrder(ProductCart productCart, Order order);
}
