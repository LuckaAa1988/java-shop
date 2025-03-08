package ru.practicum.service;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

import java.io.IOException;

public interface ProductService {
    Flux<ProductShortResponse> findAll(Integer size, String sort, String text);

    Mono<ProductFullResponse> findById(Long id) throws ProductNotFoundException;

    Mono<ProductFullResponse> addProduct(String name, String description, Double price, FilePart image) throws IOException;
}
