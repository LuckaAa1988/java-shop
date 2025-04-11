package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.entity.Product;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.mapper.ProductMapper;
import ru.practicum.repository.ProductRepository;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.ProductService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final DatabaseClient databaseClient;

    @Override
    @Cacheable(value = "products")
    public Flux<ProductShortResponse> findAll(Integer size, String sort, String text) {
        log.info("Получаем список всех товаров: size = {}, sort = {}, text = {}", size, sort, text);
        var sqlSort = "";
        switch (sort) {
            case  "ALPHABETICAL_ASC" -> sqlSort = "name ASC";
            case  "PRICE_DESC" -> sqlSort = "price DESC";
            case  "PRICE_ASC" -> sqlSort = "price ASC";
            case null, default -> sqlSort = "name DESC";
        }

        String sql = String.format("SELECT id, name, image, price FROM products" +
                " WHERE name LIKE :text OR description LIKE :text ORDER BY %s LIMIT :size", sqlSort);

        return databaseClient.sql(sql)
                .bind("text", "%" + text + "%")
                .bind("size", size)
                .map((row, metadata) -> ProductShortResponse.builder()
                        .id(row.get("id", Long.class))
                        .name(row.get("name", String.class))
                        .image(row.get("image", String.class))
                        .price(row.get("price", Double.class))
                        .build())
                        .all()
                .doOnError(error -> log.error("Ошибка при получении продуктов", error));
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Mono<ProductFullResponse> findById(Long id) {
        return productRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("Получаем товар по id {}", id))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(String.format("Товара с id %s не существует", id))))
                .map(productMapper::toFullDto)
                .doOnSuccess(response -> log.info("Товар по id {} успешно получен", id))
                .doOnError(error -> log.error("Ошибка при получении товара по id {}", id, error));
    }

    @Override
    public Mono<ProductFullResponse> addProduct(String name, String description, Double price, FilePart image) {
        String fileName = UUID.randomUUID() + "_" + image.filename();
        Path filePath = Paths.get("uploads/images/" + fileName);

        return Mono.fromCallable(() -> {
                    Files.createDirectories(filePath.getParent());
                    return filePath;
                })
                .flatMap(image::transferTo)
                .thenReturn("/images/" + fileName)
                .flatMap(imageUrl -> {
                    var product = Product.builder()
                            .name(name)
                            .description(description)
                            .price(price)
                            .image(imageUrl)
                            .build();
                    return productRepository.save(product);
                })
                .map(productMapper::toFullDto)
                .doOnSuccess(response -> log.info("Товар успешно добавлен: {}", response))
                .doOnError(error -> log.error("Ошибка при добавлении товара", error));
    }
}
