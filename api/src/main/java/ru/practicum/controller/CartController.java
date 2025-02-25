package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.CartResponse;
import ru.practicum.service.CartService;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{cartId}/add-product/{productId}")
    public ResponseEntity<CartResponse> addProductToCart(@PathVariable Long cartId,
                                                         @PathVariable Long productId,
                                                         @RequestParam Integer quantity) throws ProductNotFoundException {
        return ResponseEntity.ok(cartService.addProductToCart(cartId, productId, quantity));
    }

    @DeleteMapping("/{cartId}/delete-product/{productId}")
    public ResponseEntity<CartResponse> deleteProductFromCart(@PathVariable Long cartId,
                                                              @PathVariable Long productId) throws CartNotFoundException {
        return ResponseEntity.ok(cartService.deleteProductFromCart(cartId, productId));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long cartId) throws CartNotFoundException {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
