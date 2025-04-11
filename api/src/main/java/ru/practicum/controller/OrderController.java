package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.service.CartService;
import ru.practicum.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping
    public Flux<OrderShortResponse> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/users/{username}")
    public Flux<OrderShortResponse> findAllByUsername(@PathVariable String username) {
        return orderService.findAllByUsername(username);
    }

    @GetMapping("/{orderId}")
    public Mono<OrderFullResponse> findById(@PathVariable Long orderId) {
        return orderService.findById(orderId);
    }

    @PostMapping("/cart/{username}")
    public Mono<OrderFullResponse> createOrder(@PathVariable String username) {
        return cartService.getCartId(username)
                .flatMap(orderService::createOrder);
    }

    @GetMapping("/balance-check/{username}")
    public Mono<Double> getBalance(@PathVariable String username) {
        return orderService.getBalance(username);
    }
}
