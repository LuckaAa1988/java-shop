package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductShortResponse>> findAll(@RequestParam(defaultValue = "10") Integer size,
                                                              @RequestParam(required = false) String sort,
                                                              @RequestParam(required = false) String text) {
        return ResponseEntity.ok(productService.findAll(size, sort, text));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductFullResponse> findById(@PathVariable Long id) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductFullResponse> addProduct(@RequestParam String name,
                                                          @RequestParam String description,
                                                          @RequestParam Double price,
                                                          @RequestParam MultipartFile image) throws IOException {
        return ResponseEntity.ok(productService.addProduct(name, description, price, image));
    }
}
