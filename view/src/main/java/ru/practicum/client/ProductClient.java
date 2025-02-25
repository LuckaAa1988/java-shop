package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:9090")
public interface ProductClient {

    @GetMapping("/api/products")
    List<ProductShortResponse> findAll(@RequestParam Integer size,
                                       @RequestParam String sort,
                                       @RequestParam String text);
    @GetMapping("/api/products/{productId}")
    ProductFullResponse findById(@PathVariable Long productId);
}
