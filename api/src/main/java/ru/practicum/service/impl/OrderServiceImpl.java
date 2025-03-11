package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.entity.Order;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.repository.*;
import ru.practicum.response.CartItem;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.OrderService;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Flux<OrderShortResponse> findAll() {
        return databaseClient.sql("SELECT o.order_id AS orderId, SUM(p.price * po.quantity) AS totalSum " +
                        "FROM orders o LEFT JOIN orders_products po ON o.order_id = po.order_id " +
                        "LEFT JOIN products p ON po.product_id = p.id " +
                        "GROUP BY o.order_id")
                .map((row, metadata) -> OrderShortResponse.builder()
                        .id(row.get("orderId", Long.class))
                        .sum(row.get("totalSum", Double.class))
                        .build())
                .all()
                .doOnSubscribe(subscription -> log.info("Получаем список всех заказов"))
                .doOnError(error -> log.error("Ошибка при получении всех заказов"));
    }

    @Override
    public Mono<OrderFullResponse> findById(Long orderId) {
        return orderRepository.findById(orderId)
                .doOnSubscribe(subscription -> log.info("Получаем товар по id {}", orderId))
                .switchIfEmpty(Mono.error(new OrderNotFoundException(String.format("Заказа с id %s не существует", orderId))))
                .then(databaseClient.sql("SELECT p.id, p.name, p.image, p.price, op.quantity FROM orders_products AS op " +
                                "LEFT JOIN products AS p ON op.product_id = p.id WHERE order_id = :orderId")
                        .bind("orderId", orderId)
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
                        .map(list -> OrderFullResponse
                                .builder()
                                .orderId(orderId)
                                .products(list)
                                .build()))
                .doOnSuccess(response -> log.info("Заказ по id {} успешно получен", orderId))
                .doOnError(error -> log.error("Ошибка при получении заказа по id {}", orderId, error));
    }

    @Override
    public Mono<OrderFullResponse> createOrder(Long cartId) {
        Mono<List<CartItem>> products = databaseClient.sql("SELECT p.id, p.name, p.image, p.price, cp.quantity FROM carts_products AS cp " +
                        "LEFT JOIN products AS p ON cp.product_id = p.id WHERE cart_id = :cartId")
                .bind("cartId", cartId)
                .map((row, metadata) -> CartItem.builder()
                        .product(ProductShortResponse.builder()
                                .id(row.get("id", Long.class))
                                .image(row.get("image", String.class))
                                .name(row.get("name", String.class))
                                .price(row.get("price", Double.class))
                                .build())
                        .quantity(row.get("quantity", Integer.class))
                        .build())
                .all()
                .collectList();

        Mono<Order> order = orderRepository.save(Order.builder().createdOn(OffsetDateTime.now()).build());

        return Mono.zip(products, order)
                .flatMap(tuple -> {
                    List<CartItem> cartItems = tuple.getT1();
                    Order savedOrder = tuple.getT2();
                    Long orderId = savedOrder.getOrderId();

                    return Flux.fromIterable(cartItems)
                            .flatMap(cartItem -> databaseClient.sql("INSERT INTO orders_products (order_id, product_id, quantity) VALUES (:orderId, :productId, :quantity)")
                                    .bind("orderId", orderId)
                                    .bind("productId", cartItem.getProduct().getId())
                                    .bind("quantity", cartItem.getQuantity())
                                    .fetch()
                                    .rowsUpdated())
                            .then(Mono.just(OrderFullResponse.builder()
                                    .orderId(orderId)
                                    .products(cartItems)
                                    .build()));
                })
                .doOnSubscribe(subscription -> log.info("Создаем заказ из корзины с id {}", cartId))
                .doOnSuccess(response -> log.info("Заказ из корзины с id {} успешно создан", cartId))
                .doOnError(error -> log.error("Ошибка при создании заказа из корзины с id {}", cartId, error));
    }
}
