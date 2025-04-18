package ru.practicum.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

import java.io.IOException;

public interface ProductService {
    Flux<ProductShortResponse> findAll(Integer size, String sort, String text);

    Mono<ProductFullResponse> findById(Long id);

    Mono<ProductFullResponse> addProduct(String name, String description, Double price, FilePart image) throws IOException;
}
