package ru.practicum.service;

import org.springframework.web.multipart.MultipartFile;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductShortResponse> findAll(Integer size, String sort, String text);

    ProductFullResponse findById(Long id) throws ProductNotFoundException;

    ProductFullResponse addProduct(String name, String description, Double price, MultipartFile image) throws IOException;
}
