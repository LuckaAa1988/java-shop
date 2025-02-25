package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.ProductOrder;
import ru.practicum.entity.ProductOrderId;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, ProductOrderId> {
}
