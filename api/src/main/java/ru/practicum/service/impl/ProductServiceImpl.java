package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.entity.Product;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.mapper.ProductMapper;
import ru.practicum.repository.ProductRepository;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.ProductService;
import ru.practicum.util.SortOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static ru.practicum.repository.ProductSpecification.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductShortResponse> findAll(Integer size, String sort, String text) {
        log.info("Получаем список всех товаров: size = {}, sort = {}, text = {}", size, sort, text);
        var pageable = PageRequest.of(0, size);
        if (sort == null || sort.isBlank()) {
            sort = "ALPHABETICAL_DESC";
        }
        SortOrder sortOrder = SortOrder.valueOf(sort);
        var products = productRepository.findAll(byText(text).and(orderBy(sortOrder)), pageable);
        return products.stream()
                .map(productMapper::toShortDto)
                .toList();
    }

    @Override
    public ProductFullResponse findById(Long id) throws ProductNotFoundException {
        log.info("Получаем товар по id {}", id);
        return productMapper.toFullDto(productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(String.format("Товара с id %s не существует", id))));
    }

    @Override
    public ProductFullResponse addProduct(String name, String description, Double price, MultipartFile image) throws IOException {
        String key = UUID.randomUUID().toString();

        Path filePath = Paths.get("uploads/images/" + key);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());

        String imageUrl = "/images/" + key;

        var product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .image(imageUrl)
                .build();

        productRepository.save(product);
        return productMapper.toFullDto(product); //TODO
    }
}
