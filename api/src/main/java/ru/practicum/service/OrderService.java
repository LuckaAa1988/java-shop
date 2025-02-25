package ru.practicum.service;

import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;

import java.util.List;

public interface OrderService {
    List<OrderShortResponse> findAll();

    OrderFullResponse findById(Long orderId) throws OrderNotFoundException;

    OrderFullResponse createOrder(Long cartId) throws CartNotFoundException;
}
