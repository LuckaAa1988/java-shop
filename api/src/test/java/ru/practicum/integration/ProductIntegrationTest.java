package ru.practicum.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.service.ProductService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class ProductIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    void findAllWithoutSortAndTextSize10_ShouldReturnProductList() {
        var products = productService.findAll(10, null, null);

        assertAll(
                () -> assertNotNull(products),
                () -> assertEquals(10, products.size())
        );
    }

    @Test
    void findAllWithSortAndTextSize25_ShouldReturnProductList() {
        var products = productService.findAll(25, "PRICE_DESC", "Мощн");

        assertAll(
                () -> assertNotNull(products),
                () -> assertEquals(4, products.size()),
                () -> assertEquals("Ноутбук MacBook Pro", products.get(0).getName())
        );
    }

    @Test
    void findById_ShouldReturnProductWithId1() throws ProductNotFoundException {
        var product = productService.findById(1L);

        assertAll(
                () -> assertNotNull(product),
                () -> assertEquals(1, product.getId()),
                () -> assertEquals("Смартфон Galaxy S24", product.getName()),
                () -> assertEquals("Флагманский смартфон с мощной камерой и дисплеем 120 Гц.", product.getDescription()),
                () -> assertEquals(89999.00, product.getPrice())
        );
    }

    @Test
    void findById_ShouldReturnNotFoundException() {
        ProductNotFoundException thrown = assertThrows(ProductNotFoundException.class, () -> {
            productService.findById(100L);
        });

        assertEquals("Товара с id 100 не существует", thrown.getMessage());
    }

    @Test
    void addProduct_ShouldReturnNewProduct() throws IOException {
        var product = productService.addProduct("Продукт",
                                                "Описание",
                                                100.00,
                                                new MockMultipartFile("test", new byte[]{}));

        assertAll(
                () -> assertNotNull(product),
                () -> assertEquals(26, product.getId()),
                () -> assertEquals("Продукт", product.getName()),
                () -> assertEquals("Описание", product.getDescription()),
                () -> assertEquals(100.00, product.getPrice())
        );
    }
}
