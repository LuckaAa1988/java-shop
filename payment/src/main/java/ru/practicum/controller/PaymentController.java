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

    @GetMapping("/users/{username}")
    public Mono<UserResponse> getBalance(@PathVariable String username) {
        return paymentService.findByUsername(username);
    }

    @PatchMapping("/users/{username}")
    public Mono<UserResponse> withdraw(@PathVariable String username,
                                       @RequestParam Double amount) {
        return paymentService.withdraw(username, amount);
    }
}
