package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import request.AppUserRequest;
import ru.practicum.response.AuthResponse;

@RequiredArgsConstructor
@Component
public class AuthClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<AuthResponse> registration(String username,
                                          String password) {
        return webClientBuilder.build()
                .post()
                .uri("http://shop-api:9090/api/registration")
                .bodyValue(AppUserRequest.builder()
                        .username(username)
                        .password(password)
                        .build())
                .retrieve()
                .bodyToMono(AuthResponse.class);
    }

    public Mono<Authentication> login(String username,
                                      String password) {
        return webClientBuilder.build()
                .post()
                .uri("http://shop-api:9090/api/login")
                .bodyValue(AppUserRequest.builder()
                        .username(username)
                        .password(password)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new BadCredentialsException("Неправильные данные")))
                .bodyToMono(AuthResponse.class)
                .flatMap(authResponse -> {
                    if (authResponse.isAuthenticated()) {
                        return Mono.just(new UsernamePasswordAuthenticationToken(
                                username,
                                password,
                                authResponse.getAuthorities()
                        ));
                    }
                    return Mono.error(new BadCredentialsException("Неправильные данные"));
                });
    }
}
