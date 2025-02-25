package ru.practicum.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import ru.practicum.entity.Cart;
import ru.practicum.entity.ProductCart;
import ru.practicum.response.CartItem;
import ru.practicum.response.CartResponse;


@Mapper(componentModel = "spring")
public interface CartMapper {

    CartResponse toDto(Cart cart);
    CartItem map(ProductCart value);
}
