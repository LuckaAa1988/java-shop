package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.response.UserResponse;
import ru.practicum.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/users/{id}")
    public Mono<UserResponse> getBalance(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PatchMapping("/users/{id}")
    public Mono<UserResponse> withdraw(@PathVariable Long id,
                                       @RequestParam Double amount) {
        return paymentService.withdraw(id, amount);
    }
}
