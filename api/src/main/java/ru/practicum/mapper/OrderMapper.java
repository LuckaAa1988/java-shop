package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.entity.Order;
import ru.practicum.entity.ProductCart;
import ru.practicum.entity.ProductOrder;
import ru.practicum.response.CartItem;
import ru.practicum.response.OrderFullResponse;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderFullResponse toDto(Order order);

    ProductOrder toOrder(ProductCart productCart, Order order);

    CartItem toCartItem(ProductOrder productOrder);

    ProductOrder toOrderItem(CartItem cartItem);
}
