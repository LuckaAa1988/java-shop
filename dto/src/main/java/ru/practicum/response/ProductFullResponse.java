package ru.practicum.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFullResponse {
    Long id;
    String name;
    String description;
    String image;
    Double price;
}
