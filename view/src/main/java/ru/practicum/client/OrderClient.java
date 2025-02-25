package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;

import java.util.List;

@FeignClient(name = "order-service", url = "http://shop-api:9090")
public interface OrderClient {

    @GetMapping("/api/orders")
    List<OrderShortResponse> findAll();

    @GetMapping("/api/orders/{orderId}")
    OrderFullResponse findById(@PathVariable Long orderId);
}
