package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.ProductCart;
import ru.practicum.entity.ProductCartId;

public interface ProductCartRepository extends JpaRepository<ProductCart, ProductCartId> {
}
