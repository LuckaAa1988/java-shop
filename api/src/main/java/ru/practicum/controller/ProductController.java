package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.ProductService;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Flux<ProductShortResponse> findAll(@RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(defaultValue = "ALPHABETICAL_DESC") String sort,
                                              @RequestParam(defaultValue = "") String text) {
        return productService.findAll(size, sort, text);
    }

    @GetMapping("/{id}")
    public Mono<ProductFullResponse> findById(@PathVariable Long id) throws ProductNotFoundException {
        return productService.findById(id);
    }

    @PostMapping
    public Mono<ProductFullResponse> addProduct(@RequestParam String name,
                                                @RequestParam String description,
                                                @RequestParam Double price,
                                                @RequestPart FilePart image) throws IOException {
        return productService.addProduct(name, description, price, image);
    }
}
