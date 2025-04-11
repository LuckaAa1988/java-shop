package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practicum.client.CartClient;
import ru.practicum.client.ProductClient;
import ru.practicum.response.ProductFullResponse;

import java.security.Principal;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ProductViewController {

    private final ProductClient productClient;
    private final CartClient cartClient;

    @GetMapping
    public Mono<String> indexPage(@RequestParam(name = "size", defaultValue = "10") Integer size,
                                  @RequestParam(name = "sort", required = false) String sort,
                                  @RequestParam(name = "text", required = false) String text,
                                  ServerWebExchange exchange,
                                  Model model) {
        var products = productClient.findAll(size, sort, text).collectList();
        var usernameMono = exchange.getPrincipal()
                .map(Principal::getName)
                .switchIfEmpty(Mono.just("empty"));
        var cartItemCountMono = usernameMono.flatMap(cartClient::getCart)
                .map(cart -> cart.getProducts().size());

        return Mono.zip(products, cartItemCountMono)
                .doOnNext(tuple -> {
                    model.addAttribute("products", tuple.getT1());
                    model.addAttribute("cartItemCount", tuple.getT2());
                })
                .thenReturn("index");
    }

    @GetMapping("/products/{id}")
    public Mono<String> findById(@PathVariable Long id,
                                 ServerWebExchange exchange,
                                 Model model) {
        var product = productClient.findById(id);
        var usernameMono = exchange.getPrincipal()
                .map(Principal::getName)
                .switchIfEmpty(Mono.just("empty"));
        var cartItemCountMono = usernameMono.flatMap(cartClient::getCart)
                .map(cart -> cart.getProducts().size());

        return Mono.zip(product, cartItemCountMono)
                .doOnNext(tuple -> {
                    model.addAttribute("product", tuple.getT1());
                    model.addAttribute("cartItemCount", tuple.getT2());
                })
                .thenReturn("product");
    }

    @GetMapping("/products/add")
    public Mono<String> addPage(ServerWebExchange exchange,
                                Model model) {
        var usernameMono = exchange.getPrincipal()
                .map(Principal::getName)
                .switchIfEmpty(Mono.just("empty"));
        var cartItemCountMono = usernameMono.flatMap(cartClient::getCart)
                .map(cart -> cart.getProducts().size());

        return Mono.just(cartItemCountMono)
                .doOnNext(tuple -> {
                    model.addAttribute("product", ProductFullResponse.builder().build());
                    model.addAttribute("cartItemCount", cartItemCountMono);
                })
                .thenReturn("add");
    }

    @PostMapping("/products")
    public Mono<String> addProduct(ServerWebExchange exchange, Model model) {
        return exchange.getMultipartData()
                .flatMap(multipartData -> {
                    String name = getFirstValue(multipartData, "name");
                    String description = getFirstValue(multipartData, "description");
                    Double price = Double.valueOf(getFirstValue(multipartData, "price"));

                    FilePart imagePart = (FilePart) multipartData.getFirst("image");


                    return productClient.addProduct(name, description, price, imagePart)
                            .doOnNext(product -> model.addAttribute("product", product))
                            .thenReturn("add");
                });
    }

    private String getFirstValue(MultiValueMap<String, Part> multipartData, String key) {
        return multipartData.getFirst(key) instanceof FormFieldPart fieldPart ? fieldPart.value() : "";
    }
}
