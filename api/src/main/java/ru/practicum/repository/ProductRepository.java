package ru.practicum.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.practicum.entity.Product;

public interface ProductRepository extends R2dbcRepository<Product, Long> {
}
