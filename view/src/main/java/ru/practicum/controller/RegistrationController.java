package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practicum.client.AuthClient;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final AuthClient authClient;

    @GetMapping("/registration")
    public Mono<String> regPage() {
        return Mono.just("registration");
    }

    @PostMapping("/registration")
    public Mono<String> registration(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(form -> {
                    String username = form.getFirst("username");
                    String rawPassword = form.getFirst("password");
                    return authClient.registration(username, rawPassword)
                            .thenReturn("redirect:/");
                });
    }
}
