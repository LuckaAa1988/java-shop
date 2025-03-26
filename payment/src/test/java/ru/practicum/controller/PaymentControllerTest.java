package ru.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.Payment;
import ru.practicum.service.PaymentService;

import static org.mockito.Mockito.*;

@WebFluxTest(PaymentController.class)
@ContextConfiguration(classes = Payment.class)
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private PaymentService paymentService;

    @Test
    void findById_ShouldReturnUserResponse() {
        var userResponse = ru.practicum.response.UserResponse.builder()
                .id(1L)
                .name("Test User")
                .balance(100000.00)
                .build();

        when(paymentService.findById(1L)).thenReturn(Mono.just(userResponse));

        webTestClient.get()
                .uri("/api/payment/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test User")
                .jsonPath("$.balance").isEqualTo(100000);

        verify(paymentService, times(1)).findById(1L);
    }

    @Test
    void withdraw_ShouldReturnUserResponse() {
        var userResponse = ru.practicum.response.UserResponse.builder()
                .id(1L)
                .name("Test User")
                .balance(100000.00)
                .build();

        when(paymentService.findById(1L)).thenReturn(Mono.just(userResponse));
        when(paymentService.withdraw(1L, 10000.00)).thenReturn(Mono.just(userResponse));

        webTestClient.patch()
                .uri("/api/payment/users/1?amount=10000")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test User")
                .jsonPath("$.balance").isEqualTo(100000);

        verify(paymentService, times(1)).withdraw(1L, 10000.00);
    }

}
