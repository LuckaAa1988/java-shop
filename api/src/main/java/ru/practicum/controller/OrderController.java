package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Flux<OrderShortResponse> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public Mono<OrderFullResponse> findById(@PathVariable Long orderId) throws OrderNotFoundException {
        return orderService.findById(orderId);
    }

    @PostMapping("/cart/{cartId}")
    public Mono<OrderFullResponse> createOrder(@PathVariable Long cartId) throws CartNotFoundException {
        return orderService.createOrder(cartId);
    }
}
