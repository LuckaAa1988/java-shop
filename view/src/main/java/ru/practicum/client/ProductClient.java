package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

@RequiredArgsConstructor
@Component
public class ProductClient {

    private final WebClient webClient;

    public Flux<ProductShortResponse> findAll(Integer size,
                                              String sort,
                                              String text) {
        return webClient.get()
                .uri("/api/products?size={size}&sort={sort}&text={text}", size, sort, text)
                .retrieve()
                .bodyToFlux(ProductShortResponse.class);
    }

    public Mono<ProductFullResponse> findById(Long productId) {
        return webClient.get()
                .uri("/api/products/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductFullResponse.class);
    }

    public Mono<ProductFullResponse> addProduct(String name,
                                                String description,
                                                Double price,
                                                FilePart image) {

        return webClient.post()
                .uri("/api/products?name={name}&description={description}&price={price}", name, description, price)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(image)
                .retrieve()
                .bodyToMono(ProductFullResponse.class);
    }
}
