package ru.practicum.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCart {
    Long cartId;
    Long productId;
    Integer quantity = 0;
}
