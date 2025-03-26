package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.response.CartResponse;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.UserResponse;

@RequiredArgsConstructor
@Component
public class CartClient {

    private final WebClient webClient;

    public Mono<CartResponse> addProductToCart(Long productId,
                                               Integer quantity) {
        return webClient.post()
                .uri("/api/carts/1/add-product/{productId}?quantity={quantity}", productId, quantity)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<CartResponse> removeProductFromCart(Long productId) {
        return webClient.delete()
                .uri("/api/carts/1/delete-product/{productId}", productId)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<CartResponse> getCart() {
        return webClient.get()
                .uri("/api/carts/1")
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<OrderFullResponse> createOrder() {
        return webClient.post()
                .uri("/api/orders/cart/1")
                .retrieve()
                .bodyToMono(OrderFullResponse.class);
    }

    public Mono<Void> deleteAllFromCart() {
        return webClient.delete()
                .uri("/api/carts/1")
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<CartResponse> updateProductInCart(Long productId,
                                               Integer quantity) {
        return webClient.patch()
                .uri("/api/carts/1/update-product/{productId}?quantity={quantity}", productId, quantity)
                .retrieve()
                .bodyToMono(CartResponse.class);
    }

    public Mono<Double> getBalance() {
        return WebClient.builder()
                .baseUrl("http://payment:9091")
                .build()
                .get()
                .uri("/api/payment/users/1")
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(UserResponse::getBalance)
                .onErrorReturn(-1.00);
    }
}
