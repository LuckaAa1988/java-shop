package ru.practicum.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.practicum.entity.ProductOrder;

public interface ProductOrderRepository extends R2dbcRepository<ProductOrder, Long> {

    Flux<ProductOrder> findAllByOrderId(Long orderId);
}
