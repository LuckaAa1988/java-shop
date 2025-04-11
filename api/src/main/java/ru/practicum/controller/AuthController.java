package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import request.AppUserRequest;
import ru.practicum.entity.AppUser;
import ru.practicum.response.AuthResponse;
import ru.practicum.service.impl.AuthService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public Mono<AppUser> registration(@RequestBody AppUserRequest appUserRequest) {
        return authService.registration(appUserRequest);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AppUserRequest appUserRequest) {
        return authService.login(appUserRequest);
    }
}
