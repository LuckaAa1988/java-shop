package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
