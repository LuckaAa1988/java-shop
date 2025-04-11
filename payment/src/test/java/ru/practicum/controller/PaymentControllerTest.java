package ru.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.Payment;
import ru.practicum.configuration.SecurityConfig;
import ru.practicum.configuration.TestSecurityConfig;
import ru.practicum.service.PaymentService;

import static org.mockito.Mockito.*;

@WebFluxTest(PaymentController.class)
@ContextConfiguration(classes = Payment.class)
@Import({TestSecurityConfig.class, SecurityConfig.class})
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private PaymentService paymentService;

    @Test
    void findByUsername_ShouldReturnUserResponse() {
        var userResponse = ru.practicum.response.UserResponse.builder()
                .id(1L)
                .name("Test-User")
                .balance(100000.00)
                .build();

        when(paymentService.findByUsername("Test-User")).thenReturn(Mono.just(userResponse));

        webTestClient.get()
                .uri("/api/payment/users/Test-User")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test-User")
                .jsonPath("$.balance").isEqualTo(100000);

        verify(paymentService, times(1)).findByUsername("Test-User");
    }

    @Test
    void withdraw_ShouldReturnUserResponse() {
        var userResponse = ru.practicum.response.UserResponse.builder()
                .id(1L)
                .name("Test-User")
                .balance(100000.00)
                .build();

        when(paymentService.findByUsername("Test-User")).thenReturn(Mono.just(userResponse));
        when(paymentService.withdraw("Test-User", 10000.00)).thenReturn(Mono.just(userResponse));

        webTestClient.patch()
                .uri("/api/payment/users/Test-User?amount=10000")
                .headers(header -> header.setBearerAuth("mock-token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test-User")
                .jsonPath("$.balance").isEqualTo(100000);

        verify(paymentService, times(1)).withdraw("Test-User", 10000.00);
    }

    @Test
    void findByUsername_ShouldReturnUnAuth() {
        var userResponse = ru.practicum.response.UserResponse.builder()
                .id(1L)
                .name("Test-User")
                .balance(100000.00)
                .build();

        when(paymentService.findByUsername("Test-User")).thenReturn(Mono.just(userResponse));

        webTestClient.get()
                .uri("/api/payment/users/Test-User")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
