package ru.practicum.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.api.DefaultApi;

@TestConfiguration
public class TestApiConfig {

    @Bean
    public DefaultApi defaultApi() {
        return Mockito.mock(DefaultApi.class);
    }
}
