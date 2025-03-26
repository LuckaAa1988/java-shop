package ru.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ApiClient;
import ru.practicum.api.DefaultApi;


@Configuration
public class ApiConfig {

    @Bean
    public DefaultApi defaultApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://payment:9091");
        return new DefaultApi(apiClient);
    }
}
