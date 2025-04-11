package ru.practicum.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.test.StepVerifier;
import ru.practicum.Payment;
import ru.practicum.configuration.SecurityConfig;
import ru.practicum.configuration.TestSecurityConfig;
import ru.practicum.service.PaymentService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Payment.class)
@Import({TestSecurityConfig.class, SecurityConfig.class})
public class PaymentIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void findById_ShouldReturnUserWithId1() {
        var user = paymentService.findById(1L);

        StepVerifier.create(user)
                .expectNextMatches(p -> {
                    assertEquals(1, p.getId());
                    assertEquals("Test User", p.getName());
                    assertEquals(300000.00, p.getBalance());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void withdraw_ShouldReturnUserWithBalanceMinusAmount() {
        var user = paymentService.withdraw("Test User", 100000.00);

        StepVerifier.create(user)
                .expectNextMatches(p -> {
                    assertEquals(1, p.getId());
                    assertEquals("Test User", p.getName());
                    assertEquals(200000.00, p.getBalance());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void withdraw_ShouldReturnException() {
        StepVerifier.create(paymentService.withdraw("Test User", 1000000.00))
                .expectErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable)
                            .isInstanceOf(DataIntegrityViolationException.class);
                })
                .verify();
    }
}
