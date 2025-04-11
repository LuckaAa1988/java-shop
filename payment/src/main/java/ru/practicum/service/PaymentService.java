package ru.practicum.service;

import reactor.core.publisher.Mono;
import ru.practicum.response.UserResponse;

public interface PaymentService {
    Mono<UserResponse> findById(Long id);

    Mono<UserResponse> withdraw(String username, Double amount);

    Mono<UserResponse> findByUsername(String username);
}
