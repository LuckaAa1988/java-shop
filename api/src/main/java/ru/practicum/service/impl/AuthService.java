package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import request.AppUserRequest;
import ru.practicum.api.DefaultApi;
import ru.practicum.entity.AppUser;
import ru.practicum.entity.Cart;
import ru.practicum.repository.AppUserRepository;
import ru.practicum.repository.CartRepository;
import ru.practicum.response.AuthResponse;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private final DefaultApi defaultApi;

    public Mono<AppUser> registration(AppUserRequest appUserRequest) {
        return appUserRepository.findByUsername(appUserRequest.getUsername())
                .switchIfEmpty(Mono.defer(() ->
                        appUserRepository.save(AppUser.builder()
                                        .username(appUserRequest.getUsername())
                                        .password(passwordEncoder.encode(appUserRequest.getPassword()))
                                        .build())
                                .flatMap(user -> cartRepository.save(Cart.builder()
                                                .createdOn(OffsetDateTime.now())
                                                .appUserId(user.getId())
                                                .build())
                                        .thenReturn(user))))
                .doOnSuccess(appUser -> defaultApi.getUser(appUser.getUsername())
                        .then()
                        .subscribeOn(Schedulers.boundedElastic())
                        .subscribe());
    }

    public Mono<AuthResponse> login(AppUserRequest appUserRequest) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                appUserRequest.getUsername(),
                                appUserRequest.getPassword()
                        )).thenReturn(AuthResponse.builder()
                            .authenticated(true)
                            .build());
    }
}
