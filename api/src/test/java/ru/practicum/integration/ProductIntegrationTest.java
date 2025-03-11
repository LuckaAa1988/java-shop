package ru.practicum.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.service.ProductService;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProductIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    void findAllWithoutSortAndTextSize10_ShouldReturnProductList() {
        var products = productService.findAll(10, "ALPHABETICAL_DESC", "");
        StepVerifier.create(products)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void findAllWithSortAndTextSize25_ShouldReturnProductList() {
        var products = productService.findAll(25, "PRICE_DESC", "Мощн");

        StepVerifier.create(products)
                .expectNextMatches(product -> {
                    assertEquals("Ноутбук MacBook Pro", product.getName());
                    return true;
                })
                .expectNextMatches(product -> {
                    assertEquals("Планшет iPad Pro 12.9\"", product.getName());
                    return true;
                })
                .expectNextMatches(product -> {
                    assertEquals("Пылесос Dyson V15", product.getName());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnProductWithId1() {
        var product = productService.findById(1L);

        StepVerifier.create(product)
                .expectNextMatches(p -> {
                    assertEquals(1, p.getId());
                    assertEquals("Смартфон Galaxy S24", p.getName());
                    assertEquals("Флагманский смартфон с мощной камерой и дисплеем 120 Гц.", p.getDescription());
                    assertEquals(89999.00, p.getPrice());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnNotFoundException() {
        StepVerifier.create(productService.findById(100L))
                .expectErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable)
                            .isInstanceOf(ProductNotFoundException.class)
                            .hasMessageContaining("Товара с id 100 не существует");
                })
                .verify();
    }

    @Test
    void addProduct_ShouldReturnNewProduct() throws IOException {
        FilePart filePart = new FilePart() {
            @Override
            public String filename() {
                return "test";
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.empty();
            }

            @Override
            public String name() {
                return "test";
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return Flux.empty();
            }
        };

        productService.addProduct("Продукт", "Описание", 100.00, filePart)
                .doOnNext(product -> Assertions.assertThat(product)
                        .withFailMessage("Не должен быть равен null")
                        .isNotNull()
                        .withFailMessage("Id должен быть 26")
                        .extracting(ProductFullResponse::getId)
                        .isEqualTo(26L)
                ).block();
    }
}
