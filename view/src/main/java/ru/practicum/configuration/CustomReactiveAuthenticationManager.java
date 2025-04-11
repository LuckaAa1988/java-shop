package ru.practicum.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.practicum.client.AuthClient;

@Component
@RequiredArgsConstructor
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthClient authClient;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String rawPassword = "";
        if (authentication.getCredentials() != null) {
            rawPassword = authentication.getCredentials().toString();
        }
        String username = authentication.getName();
        return authClient.login(username, rawPassword);
    }
}
