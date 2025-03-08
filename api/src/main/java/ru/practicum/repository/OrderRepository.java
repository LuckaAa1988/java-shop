package ru.practicum.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.practicum.entity.Order;
import ru.practicum.response.OrderShortResponse;

public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @Query("SELECT o.order_id AS orderId, SUM(p.price * po.quantity) AS totalSum " +
            "FROM orders o LEFT JOIN orders_products po ON o.order_id = po.order_id " +
            "LEFT JOIN products p ON po.product_id = p.id " +
            "GROUP BY o.order_id")
    Flux<OrderShortResponse> findAllWithOrderSum();
}
