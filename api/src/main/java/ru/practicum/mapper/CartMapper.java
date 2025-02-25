package ru.practicum.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.entity.Cart;
import ru.practicum.entity.ProductCart;
import ru.practicum.response.CartItem;
import ru.practicum.response.CartResponse;

import java.util.List;


@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartMapper {

//    @Mapping(target = "products", expression = "java(productMapper.mapProductCartQuantityListToMap(cart.getProducts()))")

    CartResponse toDto(Cart cart, @Context ProductMapper productMapper);
    CartItem map(ProductCart value);
}
