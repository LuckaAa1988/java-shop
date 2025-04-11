package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.response.CartResponse;
import ru.practicum.response.OrderFullResponse;

@RequiredArgsConstructor
@Component
@Slf4j
public class CartClient {

    private final WebClient webClient;

    public Mono<CartResponse> addProductToCart(Long productId,
                                               Integer quantity,
                                               String username) {
        return webClient.post()
                .uri("/api/carts/{username}/add-product/{productId}?quantity={quantity}", username, productId, quantity)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<CartResponse> removeProductFromCart(Long productId,
                                                    String username) {
        return webClient.delete()
                .uri("/api/carts/{username}/delete-product/{productId}", username, productId)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<CartResponse> getCart(String username) {
        return webClient.get()
                .uri("/api/carts/{username}", username)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<OrderFullResponse> createOrder(String username) {
        return webClient.post()
                .uri("/api/orders/cart/{username}", username)
                .retrieve()
                .bodyToMono(OrderFullResponse.class);
    }

    public Mono<Void> deleteAllFromCart(String username) {
        return webClient.delete()
                .uri("/api/carts/{username}", username)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<CartResponse> updateProductInCart(Long productId,
                                                  Integer quantity,
                                                  String username) {
        return webClient.patch()
                .uri("/api/carts/{username}/update-product/{productId}?quantity={quantity}", username, productId, quantity)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<Double> getBalance(String username) {

        return webClient.get()
                .uri("/api/orders/balance-check/{username}", username)
                .retrieve()
                .bodyToMono(Double.class)
                .doOnError(e -> log.info(e.getMessage()))
                .onErrorReturn(-1.00);
    }
}
