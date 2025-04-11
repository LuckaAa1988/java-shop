package ru.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import ru.practicum.client.AuthClient;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         AuthClient authClient) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/", "/products", "/products/**", "/registration").permitAll()
                        .anyExchange().authenticated())
                .authenticationManager(new CustomReactiveAuthenticationManager(authClient))
                .formLogin(form -> form.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/")))
                .logout(logout -> {
                    var redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
                    redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
                    logout.logoutSuccessHandler(redirectServerLogoutSuccessHandler);
                })
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
