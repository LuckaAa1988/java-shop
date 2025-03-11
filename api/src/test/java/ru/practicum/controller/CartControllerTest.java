package ru.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.controller.CartController;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.CartResponse;
import ru.practicum.service.CartService;

import static org.mockito.Mockito.*;


@WebFluxTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private CartService cartService;


    @Test
    void addProductToCart_ShouldReturnCartResponse() {
        var cartResponse = CartResponse.builder()
                .cartId(1L)
                .build();

        when(cartService.addProductToCart(1L, 1L, 2)).thenReturn(Mono.just(cartResponse));

        webTestClient.post()
                .uri("/api/carts/1/add-product/1?quantity=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.cartId").isEqualTo(1);

        verify(cartService, times(1)).addProductToCart(any(Long.class), any(Long.class), any(Integer.class));
    }

    @Test
    void addProductToCart_ShouldThrowProductNotFoundException() {
        when(cartService.addProductToCart(1L, 1L, 2))
                .thenReturn(Mono.error(new ProductNotFoundException("Товара с id 1 не существует")));

        webTestClient.post()
                .uri("/api/carts/1/add-product/1?quantity=2")
                .exchange()
                .expectStatus().isNotFound();

        verify(cartService, times(1)).addProductToCart(1L, 1L, 2);
    }

    @Test
    void deleteProductFromCart_ShouldReturnCartResponse() {
        var cartResponse = CartResponse.builder()
                .cartId(1L)
                .build();

        when(cartService.deleteProductFromCart(1L, 1L)).thenReturn(Mono.just(cartResponse));

        webTestClient.delete()
                .uri("/api/carts/1/delete-product/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.cartId").isEqualTo(1);

        verify(cartService, times(1)).deleteProductFromCart(1L, 1L);
    }

    @Test
    void deleteProductFromCart_ShouldThrowCartNotFoundException() throws Exception {
        when(cartService.deleteProductFromCart(1L, 1L))
                .thenReturn(Mono.error(new CartNotFoundException("Корзины с id 1 не существует")));

        webTestClient.delete()
                .uri("/api/carts/1/delete-product/1")
                .exchange()
                .expectStatus().isNotFound();

        verify(cartService, times(1)).deleteProductFromCart(1L, 1L);
    }

    @Test
    void getCart_ShouldReturnCartResponse() throws Exception {
        var cartResponse = CartResponse.builder()
                .cartId(1L)
                .build();

        when(cartService.getCart(1L)).thenReturn(Mono.just(cartResponse));

        webTestClient.get()
                .uri("/api/carts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.cartId").isEqualTo(1);

        verify(cartService, times(1)).getCart(1L);
    }

    @Test
    void getCart_ShouldThrowCartNotFoundException() {
        when(cartService.getCart(1L))
                .thenReturn(Mono.error(new CartNotFoundException("Корзины с id 1 не существует")));

        webTestClient.get()
                .uri("/api/carts/1")
                .exchange()
                .expectStatus().isNotFound();

        verify(cartService, times(1)).getCart(1L);
    }

    @Test
    void deleteCart_ShouldReturnOkStatus() {
        doReturn(Mono.empty()).when(cartService).deleteCart(1L);


        webTestClient.delete()
                .uri("/api/carts/1")
                .exchange()
                .expectStatus().isOk();

        verify(cartService, times(1)).deleteCart(1L);
    }
}