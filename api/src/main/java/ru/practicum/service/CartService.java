package ru.practicum.service;

import reactor.core.publisher.Mono;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.CartResponse;

public interface CartService {
    Mono<CartResponse> getCart(Long id);

    Mono<CartResponse> addProductToCart(Long cartId, Long productId, Integer quantity);

    Mono<CartResponse> deleteProductFromCart(Long cartId, Long productId);

    Mono<Void> deleteCart(Long cartId);

    Mono<CartResponse> update(Long cartId, Long productId, Integer quantity);
}
