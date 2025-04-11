package ru.practicum.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;
import ru.practicum.App;
import ru.practicum.api.DefaultApi;
import ru.practicum.configuration.TestOathConfig;
import ru.practicum.configuration.TestRedisConfiguration;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.exception.PaymentException;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.UserResponse;
import ru.practicum.service.CartService;
import ru.practicum.service.OrderService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest(classes = App.class)
@Import({TestRedisConfiguration.class, TestOathConfig.class})
public class OrderCartIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @MockitoBean
    private DefaultApi defaultApi;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisServer redisServer;

    @BeforeEach
    void setUp() throws IOException {
        redisServer.start();
        Mockito.when(defaultApi.getUser("test")).thenReturn(Mono.just(new UserResponse().id(1).balance(1000000.00).name("test")));
        Mockito.when(defaultApi.withdraw(any(String.class), any(Double.class))).thenReturn(Mono.empty());
    }


    @AfterEach
    void end() throws IOException {
        redisServer.stop();
    }

    @Test
    void createOrder_ShouldReturnOrderWithId1() {
        cartService.addProductToCart(1L, 1L, 10).block();
        var order = orderService.createOrder(1L);

        StepVerifier.create(order)
                .expectNextMatches(o -> {
                    assertNotNull(o);
                    assertEquals(2, o.getOrderId());
                    return true;
                }).verifyComplete();
    }

    @Test
    void createOrder_ShouldReturnPaymentException() {
        cartService.addProductToCart(1L, 1L, 10000).block();
        var order = orderService.createOrder(1L);

        StepVerifier.create(order)
                .expectErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable)
                            .isInstanceOf(PaymentException.class)
                            .hasMessageContaining("Недостаточно средств");
                })
                .verify();
    }

    @Test
    void findById_ShouldReturnOrderWith1() {
        cartService.addProductToCart(1L, 1L, 1).block();
        orderService.createOrder(1L).block();
        orderService.findById(1L)
                .doOnNext(order -> Assertions.assertThat(order)
                        .withFailMessage("Не должен быть равен null")
                        .isNotNull()
                        .withFailMessage("Id должен быть равен 1")
                        .extracting(OrderFullResponse::getOrderId)
                        .isEqualTo(1L)
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
