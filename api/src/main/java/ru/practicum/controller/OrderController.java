package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<OrderShortResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderFullResponse> findById(@PathVariable Long orderId) throws OrderNotFoundException {
        return ResponseEntity.ok(orderService.findById(orderId));
    }

    @PostMapping("/cart/{cartId}")
    public ResponseEntity<OrderFullResponse> createOrder(@PathVariable Long cartId) throws CartNotFoundException {
        return ResponseEntity.ok(orderService.createOrder(cartId));
    }
}
