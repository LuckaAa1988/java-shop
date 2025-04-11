package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.response.CartResponse;
import ru.practicum.service.CartService;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{username}/add-product/{productId}")
    public Mono<CartResponse> addProductToCart(@PathVariable String username,
                                               @PathVariable Long productId,
                                               @RequestParam Integer quantity) {
        return cartService.getCartId(username)
                .flatMap(cartId -> cartService.addProductToCart(cartId, productId, quantity));
    }

    @DeleteMapping("/{username}/delete-product/{productId}")
    public Mono<CartResponse> deleteProductFromCart(@PathVariable String username,
                                                    @PathVariable Long productId) {
        return cartService.getCartId(username)
                .flatMap(cartId -> cartService.deleteProductFromCart(cartId, productId));
    }

    @GetMapping("/{username}")
    public Mono<CartResponse> getCart(@PathVariable String username) {
        return cartService.getCartId(username)
                .flatMap(cartService::getCart);
    }

    @DeleteMapping("/{username}")
    public Mono<Void> deleteCart(@PathVariable String username) {
        return cartService.getCartId(username)
                .flatMap(cartService::deleteCart);
    }

    @PatchMapping("/{username}/update-product/{productId}")
    public Mono<CartResponse> updateProductInCart(@PathVariable String username,
                                                  @PathVariable Long productId,
                                                  @RequestParam Integer quantity) {
        return cartService.getCartId(username)
                .flatMap(cartId -> cartService.update(cartId, productId, quantity));
    }
}
