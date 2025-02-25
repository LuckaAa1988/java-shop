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
@Table(name = "orders_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrder {
    @EmbeddedId
    private ProductOrderId id;
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    Order order;
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    Product product;
    Integer quantity = 0;
}
