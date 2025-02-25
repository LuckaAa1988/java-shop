package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.response.OrderShortResponse;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:9090")
public interface OrderClient {

    @GetMapping("/api/orders")
    List<OrderShortResponse> findAll();
}
