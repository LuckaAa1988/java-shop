package ru.practicum.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFullResponse {
    Long id;
    Map<ProductShortResponse, Integer> products = new HashMap<>();
}
