package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.controller.ProductController;
import ru.practicum.exception.ExceptionApiHandler;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.response.ProductFullResponse;
import ru.practicum.response.ProductShortResponse;
import ru.practicum.service.ProductService;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(ExceptionApiHandler.class)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_ShouldReturnListOfProductShortResponses() throws Exception {
        ProductShortResponse productShortResponse = new ProductShortResponse();
        productShortResponse.setId(1L);
        productShortResponse.setName("Test Product");

        when(productService.findAll(any(Integer.class), any(), any()))
                .thenReturn(List.of(productShortResponse));

        mockMvc.perform(get("/api/products")
                        .param("size", "10")
                        .param("sort", "ALPHABETICAL_DESC")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(productShortResponse))));

        verify(productService, times(1)).findAll(10, "ALPHABETICAL_DESC", "test");
    }

    @Test
    void findById_ShouldReturnProductFullResponse() throws Exception {
        ProductFullResponse productFullResponse = new ProductFullResponse();
        productFullResponse.setId(1L);
        productFullResponse.setName("Test Product");

        when(productService.findById(1L)).thenReturn(productFullResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productFullResponse)));

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowProductNotFoundException() throws Exception {
        when(productService.findById(1L)).thenThrow(new ProductNotFoundException("Товара с id 1 не существует"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findById(1L);
    }

    @Test
    void addProduct_ShouldReturnProductFullResponse() throws Exception {
        ProductFullResponse productFullResponse = new ProductFullResponse();
        productFullResponse.setId(1L);
        productFullResponse.setName("Test Product");

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        when(productService.addProduct(any(String.class), any(String.class), any(Double.class), any(MultipartFile.class)))
                .thenReturn(productFullResponse);

        mockMvc.perform(multipart("/api/products")
                        .file(image)
                        .param("name", "Test Product")
                        .param("description", "Test Description")
                        .param("price", "100.0"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productFullResponse)));

        verify(productService, times(1))
                .addProduct("Test Product", "Test Description", 100.0, image);
    }

    @Test
    void addProduct_ShouldThrowIOException() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        when(productService.addProduct(any(String.class), any(String.class), any(Double.class), any(MultipartFile.class)))
                .thenThrow(new IOException("Failed to read file"));

        mockMvc.perform(multipart("/api/products")
                        .file(image)
                        .param("name", "Test Product")
                        .param("description", "Test Description")
                        .param("price", "100.0"))
                .andExpect(status().isInternalServerError());

        verify(productService, times(1))
                .addProduct("Test Product", "Test Description", 100.0, image);
    }
}