package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

import java.util.List;

@FeignClient(name = "product-service", url = "http://shop-api:9090")
public interface ProductClient {

    @GetMapping("/api/products")
    List<ProductShortResponse> findAll(@RequestParam Integer size,
                                       @RequestParam String sort,
                                       @RequestParam String text);
    @GetMapping("/api/products/{productId}")
    ProductFullResponse findById(@PathVariable Long productId);

    @PostMapping(value = "/api/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ProductFullResponse addProduct(@RequestPart("name") String name,
                                   @RequestPart("description") String description,
                                   @RequestPart("price") Double price,
                                   @RequestPart("image") MultipartFile image);
}
