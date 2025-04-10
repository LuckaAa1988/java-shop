package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.client.CartClient;

import java.security.Principal;


@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private final CartClient cartClient;

    @PostMapping(value = "/add")
    public Mono<String> addToCart(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    Long productId = Long.valueOf(formData.getFirst("productId"));
                    Integer quantity = Integer.valueOf(formData.getFirst("quantity"));
                    return exchange.getPrincipal()
                            .map(Principal::getName)
                            .flatMap(username -> cartClient.addProductToCart(productId, quantity, username)
                                    .thenReturn("redirect:/cart"));
                });
    }


    @PostMapping("/remove")
    public Mono<String> removeFromCart(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    Long productId = Long.valueOf(formData.getFirst("productId"));
                    return exchange.getPrincipal()
                            .map(Principal::getName)
                            .flatMap(username -> cartClient.removeProductFromCart(productId, username)
                                    .thenReturn("redirect:/cart"));
                });
    }

    @PostMapping("/update")
    public Mono<String> updateCart(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    Long productId = Long.valueOf(formData.getFirst("productId"));
                    Integer quantity = Integer.valueOf(formData.getFirst("quantity"));
                    return exchange.getPrincipal()
                            .map(Principal::getName)
                            .flatMap(username -> cartClient.updateProductInCart(productId, quantity, username)
                                    .thenReturn("redirect:/cart"));
                });
    }

    @GetMapping
    public Mono<String> cartPage(ServerWebExchange exchange,
                                 Model model) {
        var usernameMono = exchange.getPrincipal()
                .map(Principal::getName);

        var cart = usernameMono.flatMap(cartClient::getCart);

        var cartItemCountMono = usernameMono.flatMap(username -> cartClient.getCart(username)
                .map(cartResponse -> cartResponse.getProducts().size()));

        var sumMono = cart
                .flatMapMany(cartResponse -> Flux.fromIterable(cartResponse.getProducts()))
                .map(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .reduce(0.0, Double::sum);

        var balance = usernameMono.flatMap(cartClient::getBalance);

        return Mono.zip(cart, sumMono, cartItemCountMono, balance)
                .doOnNext(tuple -> {
                    model.addAttribute("cart", tuple.getT1());
                    model.addAttribute("sum", tuple.getT2());
                    model.addAttribute("cartItemCount", tuple.getT3());
                    model.addAttribute("balance", tuple.getT4());
                })
                .thenReturn("cart");
    }

    @PostMapping("/checkout")
    public Mono<String> createOrder(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> cartClient.createOrder(username)
                        .flatMap(order -> cartClient.deleteAllFromCart(username)
                                .thenReturn(order))
                        .map(order -> "redirect:/orders/" + order.getOrderId()));
    }
}
