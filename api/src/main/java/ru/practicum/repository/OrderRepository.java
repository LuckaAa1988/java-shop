package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Order;
import ru.practicum.response.OrderShortResponse;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT new ru.practicum.response.OrderShortResponse(o.orderId, SUM(po.product.price * po.quantity))" +
            " FROM Order o LEFT JOIN ProductOrder po ON o.orderId = po.order.orderId GROUP BY o.orderId")
    List<OrderShortResponse> findAllWithOrderSum();
}
