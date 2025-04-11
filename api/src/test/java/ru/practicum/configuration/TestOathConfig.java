package ru.practicum.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestOathConfig {

    @Bean
    @Primary
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return mock(ReactiveClientRegistrationRepository.class);
    }

    @Bean
    @Primary
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager() {
        return mock(ReactiveOAuth2AuthorizedClientManager.class);
    }

    @Bean
    @Primary
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return mock(ServerOAuth2AuthorizedClientRepository.class);
    }

    @Bean
    @Primary
    public ReactiveJwtDecoder jwtDecoder() {
        ReactiveJwtDecoder mockDecoder = mock(ReactiveJwtDecoder.class);
        Mockito.when(mockDecoder.decode(Mockito.anyString()))
                .thenReturn(Mono.just(mock(Jwt.class)));
        return mockDecoder;
    }
}

