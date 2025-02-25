package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.response.CartResponse;
import ru.practicum.response.OrderFullResponse;

@FeignClient(name = "cart-service", url = "http://shop-api:9090")
public interface CartClient {

    @PostMapping("/api/carts/1/add-product/{productId}")
    void addProductToCart(@PathVariable Long productId,
                          @RequestParam Integer quantity);

    @DeleteMapping("/api/carts/1/delete-product/{productId}")
    void removeProductFromCart(@PathVariable Long productId);

    @GetMapping("/api/carts/1")
    CartResponse getCart();

    @PostMapping("/api/orders/cart/1")
    OrderFullResponse createOrder();

    @DeleteMapping("/api/carts/1")
    void deleteAllFromCart();
}
