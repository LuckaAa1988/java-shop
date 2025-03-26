package ru.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.App;
import ru.practicum.controller.ProductController;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.ProductService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@WebFluxTest(ProductController.class)
@ContextConfiguration(classes = App.class)
public class ProductControllerTest {

    @MockitoBean
    private ProductService productService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void findAll_ShouldReturnListOfProductShortResponses() throws Exception {
        var productShortResponse = ProductShortResponse.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productService.findAll(any(Integer.class), any(), any()))
                .thenReturn(Flux.just(productShortResponse));

        webTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id");

        verify(productService, times(1)).findAll(10, "ALPHABETICAL_DESC", "");
    }

    @Test
    void findById_ShouldReturnProductFullResponse() throws Exception {
        var productFullResponse = ProductFullResponse.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productService.findById(1L)).thenReturn(Mono.just(productFullResponse));

        webTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Product");

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowProductNotFoundException() throws Exception {
        when(productService.findById(1L)).thenReturn(Mono.error(new ProductNotFoundException("Товара с id 1 не существует")));

        webTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isNotFound();

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void addProduct_ShouldReturnProductFullResponse() throws Exception {
        var productFullResponse = ProductFullResponse.builder()
                .id(1L)
                .name("Test Product")
                .build();

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("image", new ByteArrayResource("test image content".getBytes()))
                .filename("test-image.jpg");

        when(productService.addProduct(any(String.class), any(String.class), any(Double.class), any(FilePart.class)))
                .thenReturn(Mono.just(productFullResponse));

        webTestClient.post()
                .uri("/api/products?name=test&description=test&price=10.0")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void addProduct_ShouldThrowIOException() throws Exception {
        when(productService.addProduct(any(String.class), any(String.class), any(Double.class), any(FilePart.class)))
                .thenThrow(new IOException("Failed to read file"));

        webTestClient.post()
                .uri("/api/products")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}