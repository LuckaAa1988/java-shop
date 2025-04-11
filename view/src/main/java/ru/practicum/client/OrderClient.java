package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;

@RequiredArgsConstructor
@Component
public class OrderClient {

    private final WebClient webClient;

    public Flux<OrderShortResponse> findAllByUsername(String username) {
        return webClient.get()
                .uri("/api/orders/users/{username}", username)
                .retrieve()
                .bodyToFlux(OrderShortResponse.class);
    }

    public Mono<OrderFullResponse> findById(Long orderId) {
        return webClient.get()
                .uri("/api/orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(OrderFullResponse.class);
    }
}
