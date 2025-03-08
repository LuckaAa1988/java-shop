package ru.practicum.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrder {
    Long orderId;
    Long productId;
    Integer quantity = 0;
}
