package ru.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.ApiClient;
import ru.practicum.api.DefaultApi;


@Configuration
public class ApiConfig {

    @Bean
    public ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {

        var oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId("api");
        return oauth2;
    }

    @Bean
    public DefaultApi defaultApi(ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client) {
        WebClient webClient = WebClient.builder().filter(oauth2Client).build();
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath("http://payment:9091");
        return new DefaultApi(apiClient);
    }
}
