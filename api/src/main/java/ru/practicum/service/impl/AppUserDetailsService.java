package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.repository.AppUserRepository;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements ReactiveUserDetailsService {

    private final AppUserRepository appUserRepository;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Пользователь не найден")))
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build());
    }
}
