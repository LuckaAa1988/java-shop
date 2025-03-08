package ru.practicum.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.practicum.entity.ProductCart;

public interface ProductCartRepository extends R2dbcRepository<ProductCart, Long> {

    Flux<ProductCart> findAllByCartId(Long cartId);
    Flux<ProductCart> findByCartId(Long cartId);
}
