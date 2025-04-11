package ru.practicum.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.practicum.entity.Cart;

public interface CartRepository extends R2dbcRepository<Cart, Long> {

    @Query("SELECT c.cart_id AS cartId " +
            "FROM carts as c LEFT JOIN app_users au ON c.app_user_id = au.id " +
            "WHERE au.username = :username " +
            "GROUP BY c.cart_id")
    Mono<Long> findByUsername(String username);
}
