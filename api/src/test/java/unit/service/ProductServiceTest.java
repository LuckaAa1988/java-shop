package unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.entity.Order;
import ru.practicum.entity.Product;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.mapper.ProductMapper;
import ru.practicum.repository.ProductRepository;
import ru.practicum.repository.ProductSpecification;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.impl.ProductServiceImpl;
import ru.practicum.util.SortOrder;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductServiceImpl productService;
    private Product product;
    private ProductShortResponse productShortResponse;
    private ProductFullResponse productFullResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(100.0)
                .image("/images/test_image.jpg")
                .build();

        productShortResponse = new ProductShortResponse();
        productShortResponse.setId(1L);
        productShortResponse.setName("Test Product");

        productFullResponse = new ProductFullResponse();
        productFullResponse.setId(1L);
        productFullResponse.setName("Test Product");
        productFullResponse.setDescription("Test Description");
        productFullResponse.setPrice(100.0);
        productFullResponse.setImage("/images/test_image.jpg");
    }

    @Test
    void findAll_ShouldReturnListOfProductShortResponses() {
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(
                Mockito.<Specification<Product>>any(),
                any(Pageable.class)))
                .thenReturn(productPage);

        when(productMapper.toShortDto(product)).thenReturn(productShortResponse);

        List<ProductShortResponse> result = productService.findAll(10, "ALPHABETICAL_DESC", "test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productShortResponse, result.get(0));

        verify(productRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(productMapper, times(1)).toShortDto(product);
    }

    @Test
    void findById_ShouldReturnProductFullResponse() throws ProductNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toFullDto(product)).thenReturn(productFullResponse);

        ProductFullResponse result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(productFullResponse, result);

        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).toFullDto(product);
    }

    @Test
    void findById_ShouldThrowProductNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));

        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, never()).toFullDto(any());
    }

    @Test
    void addProduct_ShouldReturnProductFullResponse() throws IOException {
        try (MockedStatic<Product> mockedProduct = mockStatic(Product.class)) {
            Product.ProductBuilder builderMock = mock(Product.ProductBuilder.class);

            when(Product.builder()).thenReturn(builderMock);
            when(builderMock.id(anyLong())).thenReturn(builderMock);
            when(builderMock.name(any(String.class))).thenReturn(builderMock);
            when(builderMock.description(any(String.class))).thenReturn(builderMock);
            when(builderMock.price(any(Double.class))).thenReturn(builderMock);
            when(builderMock.image(any(String.class))).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(product);

            Product result = Product.builder()
                    .id(1L)
                    .name("Test Product")
                    .description("Test Description")
                    .price(100.0)
                    .image("/images/test_image.jpg")
                    .build();

            assertSame(product, result);

            MultipartFile image = mock(MultipartFile.class);
            when(image.getOriginalFilename()).thenReturn("test_image.jpg");
            when(image.getBytes()).thenReturn(new byte[0]);

            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productMapper.toFullDto(product)).thenReturn(productFullResponse);

            ProductFullResponse res = productService.addProduct("Test Product", "Test Description", 100.0, image);

            assertNotNull(res);
            assertEquals(productFullResponse, res);

            verify(productRepository, times(1)).save(any(Product.class));
            verify(productMapper, times(1)).toFullDto(product);
        }
    }

    @Test
    void addProduct_ShouldThrowIOException() throws IOException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.getOriginalFilename()).thenReturn("test_image.jpg");
        when(image.getBytes()).thenThrow(new IOException("Failed to read file"));

        assertThrows(IOException.class, () -> productService.addProduct("Test Product", "Test Description", 100.0, image));

        verify(productRepository, never()).save(any(Product.class));
        verify(productMapper, never()).toFullDto(any());
    }
}