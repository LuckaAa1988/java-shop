package ru.practicum.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;

public interface OrderService {
    Flux<OrderShortResponse> findAll();

    Mono<OrderFullResponse> findById(Long orderId);

    Mono<OrderFullResponse> createOrder(Long cartId);

    Flux<OrderShortResponse> findAllByUsername(String username);

    Mono<Double> getBalance(String username);
}
