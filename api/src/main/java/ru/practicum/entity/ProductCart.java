package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCart {
    @EmbeddedId
    private ProductCartId id;
    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    Cart cart;
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;
    Integer quantity = 0;
}
