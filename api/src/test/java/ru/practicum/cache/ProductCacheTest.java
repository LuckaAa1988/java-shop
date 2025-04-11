package ru.practicum.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;
import ru.practicum.App;
import ru.practicum.configuration.TestOathConfig;
import ru.practicum.configuration.TestRedisConfiguration;
import ru.practicum.repository.ProductRepository;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.service.impl.ProductServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = App.class)
@Import({TestRedisConfiguration.class, TestOathConfig.class})
public class ProductCacheTest {

    @Autowired
    private ProductServiceImpl productService;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisServer redisServer;

    @AfterEach
    void end() throws IOException {
        redisServer.stop();
    }


    @Test
    void findById_ShouldReturnProductWithId1AndUseCache() {
        cacheManager.getCache("products").clear();

        var product1 = productService.findById(1L);

        StepVerifier.create(product1)
                .expectNextMatches(p -> {
                    assertEquals(1, p.getId());
                    assertEquals("Смартфон Galaxy S24", p.getName());
                    assertEquals("Флагманский смартфон с мощной камерой и дисплеем 120 Гц.", p.getDescription());
                    assertEquals(89999.00, p.getPrice());
                    return true;
                })
                .verifyComplete();

        var cachedValue = cacheManager.getCache("products").get(1L, ProductFullResponse.class);
        assertNotNull(cachedValue);
        assertEquals("Смартфон Galaxy S24", cachedValue.getName());

        var product2 = productService.findById(1L);

        StepVerifier.create(product2)
                .expectNextMatches(p -> {
                    assertEquals(1, p.getId());
                    assertEquals("Смартфон Galaxy S24", p.getName());
                    return true;
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(1L);
    }


}
