package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long id) throws CartNotFoundException;

    CartResponse addProductToCart(Long cartId, Long productId, Integer quantity) throws ProductNotFoundException;

    CartResponse deleteProductFromCart(Long cartId, Long productId) throws CartNotFoundException;

    void deleteCart(Long cartId);
}
