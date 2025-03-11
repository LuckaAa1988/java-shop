package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.repository.CartRepository;
import ru.practicum.repository.ProductCartRepository;
import ru.practicum.response.CartItem;
import ru.practicum.response.CartResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.CartService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductCartRepository productCartRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<CartResponse> getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .doOnSubscribe(subscription -> log.info("Получаем корзину по id {}", cartId))
                .switchIfEmpty(Mono.error(new CartNotFoundException(String.format("Корзины с id %s не существует", cartId))))
                .then(databaseClient.sql("SELECT p.id, p.name, p.image, p.price, cp.quantity FROM carts_products AS cp " +
                                "LEFT JOIN products AS p ON cp.product_id = p.id WHERE cart_id = :cartId")
                        .bind("cartId", cartId)
                        .map((row, metadata) -> CartItem.builder()
                                .product(ProductShortResponse.builder()
                                        .id(row.get("id", Long.class))
                                        .name(row.get("name", String.class))
                                        .image(row.get("image", String.class))
                                        .price(row.get("price", Double.class))
                                        .build())
                                .quantity(row.get("quantity", Integer.class))
                                .build())
                        .all()
                        .collectList()
                        .map(list -> CartResponse
                                .builder()
                                .cartId(cartId)
                                .products(list)
                                .build()))
                .doOnSuccess(response -> log.info("Корзина по id {} успешно получена", cartId))
                .doOnError(error -> log.error("Ошибка при получении корзины по id {}", cartId, error));
    }

    @Override
    public Mono<CartResponse> addProductToCart(Long cartId, Long productId, Integer quantity) {
        return databaseClient.sql("INSERT INTO carts_products (cart_id, product_id, quantity) VALUES (:cartId, :productId, :quantity)")
                .bind("cartId", cartId)
                .bind("productId", productId)
                .bind("quantity", quantity)
                .fetch()
                .rowsUpdated()
                .flatMap(rows -> getCart(cartId))
                .doOnSubscribe(subscription -> log.info("Добавляем продукт с id {} в корзину с id {}", productId, cartId))
                .doOnSuccess(response -> log.info("Продукт с id {} в корзине по id {} успешно добавлен", productId, cartId))
                .onErrorResume(DuplicateKeyException.class, e -> update(cartId, productId, quantity));
    }

    @Override
    public Mono<CartResponse> deleteProductFromCart(Long cartId, Long productId) {
        return databaseClient.sql("DELETE FROM carts_products AS cp WHERE cart_id = :cartId AND product_id = :productId")
                .bind("cartId", cartId)
                .bind("productId", productId)
                .fetch()
                .rowsUpdated()
                .doOnSubscribe(subscription -> log.info("Удаляем продукт с id {} в корзине с id {}", productId, cartId))
                .doOnSuccess(response -> log.info("Продукт с id {} в корзине по id {} успешно удален", productId, cartId))
                .doOnError(error -> log.error("Ошибка при удалении продукта с id {} из корзины по id {}", productId, cartId, error))
                .then(getCart(cartId));
    }

    @Override
    public Mono<Void> deleteCart(Long cartId) {
        return productCartRepository.deleteAll()
                .doOnSubscribe(subscription -> log.info("Удаляем все продукты в корзине с id {}", cartId))
                .doOnSuccess(response -> log.info("Корзина по id {} успешно удалена", cartId))
                .doOnError(error -> log.error("Ошибка при удалении корзины по id {}", cartId, error));
    }

    @Override
    public Mono<CartResponse> update(Long cartId, Long productId, Integer quantity) {
        return databaseClient.sql("UPDATE carts_products SET quantity = :quantity WHERE (product_id = :productId AND cart_id = :cartId)")
                .bind("cartId", cartId)
                .bind("productId", productId)
                .bind("quantity", quantity)
                .fetch()
                .rowsUpdated()
                .doOnSubscribe(subscription -> log.info("Обнолвяем продукты в корзине с id {}", cartId))
                .doOnSuccess(response -> log.info("Продукты в корзине по id {} успешно обновлены", cartId))
                .doOnError(error -> log.error("Ошибка при обновлении корзины по id {}", cartId, error))
                .then(getCart(cartId));
    }
}
