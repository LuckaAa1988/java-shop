//package unit.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.practicum.controller.CartController;
//import ru.practicum.exception.CartNotFoundException;
//import ru.practicum.exception.ExceptionApiHandler;
//import ru.practicum.exception.ProductNotFoundException;
//import ru.practicum.response.CartResponse;
//import ru.practicum.service.CartService;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//public class CartControllerTest {
//
//    @Mock
//    private CartService cartService;
//
//    @InjectMocks
//    private CartController cartController;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
//                .setControllerAdvice(ExceptionApiHandler.class)
//                .build();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void addProductToCart_ShouldReturnCartResponse() throws Exception {
//        CartResponse cartResponse = new CartResponse();
//        cartResponse.setCartId(1L);
//
//        when(cartService.addProductToCart(1L, 1L, 2)).thenReturn(cartResponse);
//
//        mockMvc.perform(post("/api/carts/1/add-product/1")
//                        .param("quantity", "2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(cartResponse)));
//
//        verify(cartService, times(1)).addProductToCart(1L, 1L, 2);
//    }
//
//    @Test
//    void addProductToCart_ShouldThrowProductNotFoundException() throws Exception {
//        when(cartService.addProductToCart(1L, 1L, 2))
//                .thenThrow(new ProductNotFoundException("Товара с id 1 не существует"));
//
//        mockMvc.perform(post("/api/carts/1/add-product/1")
//                        .param("quantity", "2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//
//        verify(cartService, times(1)).addProductToCart(1L, 1L, 2);
//    }
//
//    @Test
//    void deleteProductFromCart_ShouldReturnCartResponse() throws Exception {
//        CartResponse cartResponse = new CartResponse();
//        cartResponse.setCartId(1L);
//
//        when(cartService.deleteProductFromCart(1L, 1L)).thenReturn(cartResponse);
//
//        mockMvc.perform(delete("/api/carts/1/delete-product/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(cartResponse)));
//
//        verify(cartService, times(1)).deleteProductFromCart(1L, 1L);
//    }
//
//    @Test
//    void deleteProductFromCart_ShouldThrowCartNotFoundException() throws Exception {
//        when(cartService.deleteProductFromCart(1L, 1L))
//                .thenThrow(new CartNotFoundException("Корзины с id 1 не существует"));
//
//        mockMvc.perform(delete("/api/carts/1/delete-product/1"))
//                .andExpect(status().isNotFound());
//
//        verify(cartService, times(1)).deleteProductFromCart(1L, 1L);
//    }
//
//    @Test
//    void getCart_ShouldReturnCartResponse() throws Exception {
//        CartResponse cartResponse = new CartResponse();
//        cartResponse.setCartId(1L);
//
//        when(cartService.getCart(1L)).thenReturn(cartResponse);
//
//        mockMvc.perform(get("/api/carts/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(cartResponse)));
//
//        verify(cartService, times(1)).getCart(1L);
//    }
//
//    @Test
//    void getCart_ShouldThrowCartNotFoundException() throws Exception {
//        when(cartService.getCart(1L))
//                .thenThrow(new CartNotFoundException("Корзины с id 1 не существует"));
//
//        mockMvc.perform(get("/api/carts/1"))
//                .andExpect(status().isNotFound());
//
//        verify(cartService, times(1)).getCart(1L);
//    }
//
//    @Test
//    void deleteCart_ShouldReturnOkStatus() throws Exception {
//        doNothing().when(cartService).deleteCart(1L);
//
//        mockMvc.perform(delete("/api/carts/1"))
//                .andExpect(status().isOk());
//
//        verify(cartService, times(1)).deleteCart(1L);
//    }
//}