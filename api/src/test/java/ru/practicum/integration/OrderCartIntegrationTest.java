package ru.practicum.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.service.CartService;
import ru.practicum.service.OrderService;


@SpringBootTest
public class OrderCartIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;


    @Test
    void createOrder_ShouldReturnOrderWithId1() {
        cartService.addProductToCart(1L, 1L, 10)
                .flatMap(cart -> orderService.createOrder(1L))
                .doOnNext(order -> Assertions.assertThat(order)
                        .withFailMessage("Не должен быть равен null")
                        .isNotNull()
                        .withFailMessage("Id должен быть 1")
                        .extracting(OrderFullResponse::getOrderId)
                        .isEqualTo(1L)
                ).block();
    }

    @Test
    void findById_ShouldReturnOrderWith1() {
        orderService.createOrder(1L)
                .doOnNext(order -> Assertions.assertThat(order)
                        .withFailMessage("Не должен быть равен null")
                        .isNotNull()
                        .withFailMessage("Id должен быть 2")
                        .extracting(OrderFullResponse::getOrderId)
                        .isEqualTo(2L)
                ).block();
    }

    @Test
    void findById_ShouldThrowNotFoundException() {
        StepVerifier.create(orderService.findById(100L))
                .expectErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable)
                            .isInstanceOf(OrderNotFoundException.class)
                            .hasMessageContaining("Заказа с id 100 не существует");
                })
                .verify();
    }
}
