package ru.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.App;
import ru.practicum.configuration.SecurityConfig;
import ru.practicum.configuration.TestSecurityConfig;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.service.impl.CartServiceImpl;
import ru.practicum.service.impl.OrderServiceImpl;

import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
@ContextConfiguration(classes = App.class)
@Import({TestSecurityConfig.class, SecurityConfig.class})
public class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderServiceImpl orderService;

    @MockitoBean
    private CartServiceImpl cartService;


    @Test
    void findAll_ShouldReturnListOfOrderShortResponses() {
        var orderShortResponse = OrderShortResponse.builder()
                .id(1L)
                .build();

        when(orderService.findAll()).thenReturn(Flux.just((orderShortResponse)));

        webTestClient.get()
                .uri("/api/orders")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(1);

        verify(orderService, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnOrderFullResponse() {
        var orderFullResponse = OrderFullResponse.builder()
                .orderId(1L)
                .build();

        when(orderService.findById(1L)).thenReturn(Mono.just(orderFullResponse));

        webTestClient.get()
                .uri("/api/orders/1")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo(1);

        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowOrderNotFoundException() {
        when(orderService.findById(1L)).thenReturn(Mono.error(new OrderNotFoundException("Товара с id 1 не существует")));


        webTestClient.get()
                .uri("/api/orders/1")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isNotFound();

        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void createOrder_ShouldReturnOrderFullResponse() {
        var orderFullResponse = OrderFullResponse.builder()
                .orderId(1L)
                .build();

        when(orderService.createOrder(1L)).thenReturn(Mono.just(orderFullResponse));
        when(cartService.getCartId("test")).thenReturn(Mono.just(1L));

        webTestClient.post()
                .uri("/api/orders/cart/test")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo(1);

        verify(orderService, times(1)).createOrder(1L);
    }

    @Test
    void createOrder_ShouldThrowCartNotFoundException() {
        when(cartService.getCartId("test")).thenReturn(Mono.just(1L));
        when(orderService.createOrder(1L)).thenReturn(Mono.error(new CartNotFoundException("Корзины с id 1 не существует")));

        webTestClient.post()
                .uri("/api/orders/cart/test")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isNotFound();

        verify(orderService, times(1)).createOrder(1L);
    }

    @Test
    void findById_ShouldReturnUnAuth() {
        var orderFullResponse = OrderFullResponse.builder()
                .orderId(1L)
                .build();

        when(orderService.findById(1L)).thenReturn(Mono.just(orderFullResponse));

        webTestClient.get()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}