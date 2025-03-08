package ru.practicum.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.practicum.entity.Cart;

public interface CartRepository extends R2dbcRepository<Cart, Long> {
}
