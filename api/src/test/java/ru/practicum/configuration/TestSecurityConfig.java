package ru.practicum.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return token -> Mono.just(Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", "test-user")
                .claim("scope", "profile")
                .claim("preferred_username", "test-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build());
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> Mono.just(
                User.withUsername("test")
                        .password("{noop}password")
                        .roles("USER")
                        .build()
        );
    }
}
