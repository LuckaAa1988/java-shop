package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
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
    public Mono<CartResponse> addProductToCart(@PathVariable Long cartId,
                                               @PathVariable Long productId,
                                               @RequestParam Integer quantity) {
        return cartService.addProductToCart(cartId, productId, quantity);
    }

    @DeleteMapping("/{cartId}/delete-product/{productId}")
    public Mono<CartResponse> deleteProductFromCart(@PathVariable Long cartId,
                                                              @PathVariable Long productId) {
        return cartService.deleteProductFromCart(cartId, productId);
    }

    @GetMapping("/{cartId}")
    public Mono<CartResponse> getCart(@PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    @DeleteMapping("/{cartId}")
    public Mono<Void> deleteCart(@PathVariable Long cartId) {
        return cartService.deleteCart(cartId);
    }

    @PatchMapping("/{cartId}/update-product/{productId}")
    public Mono<CartResponse> updateProductInCart(@PathVariable Long cartId,
                                                  @PathVariable Long productId,
                                                  @RequestParam Integer quantity) {
        return cartService.update(cartId, productId, quantity);
    }
}
