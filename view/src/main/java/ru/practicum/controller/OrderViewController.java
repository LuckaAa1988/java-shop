package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practicum.client.CartClient;
import ru.practicum.client.OrderClient;
import ru.practicum.response.OrderShortResponse;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderClient orderClient;
    private final CartClient cartClient;

    @GetMapping
    public Mono<String> ordersPage(ServerWebExchange exchange,
                                   Model model) {
        var orders = exchange.getPrincipal()
                .map(Principal::getName)
                .map(orderClient::findAllByUsername);


        var sumMono = orders.flatMap(o -> o
                .map(OrderShortResponse::getSum)
                .reduce(0.0, Double::sum));

        var cartItemCountMono = exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> cartClient.getCart(username)
                .map(cart -> cart.getProducts().size()));

        return Mono.zip(orders, sumMono, cartItemCountMono)
                .doOnNext(tuple -> {
                    model.addAttribute("orders", tuple.getT1());
                    model.addAttribute("sum", tuple.getT2());
                    model.addAttribute("cartItemCount", tuple.getT3());
                })
                .thenReturn("orders");
    }

    @GetMapping("/{orderId}")
    public Mono<String> orderPage(@PathVariable Long orderId,
                                  ServerWebExchange exchange,
                                  Model model) {
        var order = orderClient.findById(orderId);
        var cartItemCountMono = exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> cartClient.getCart(username)
                        .map(cart -> cart.getProducts().size()));
        return Mono.zip(order, cartItemCountMono)
                .doOnNext(tuple -> {
                    model.addAttribute("order", tuple.getT1());
                    model.addAttribute("cartItemCount", tuple.getT2());
                })
                .thenReturn("order");
    }
}
