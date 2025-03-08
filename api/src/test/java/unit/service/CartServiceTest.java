//package unit.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.practicum.entity.*;
//import ru.practicum.exception.CartNotFoundException;
//import ru.practicum.exception.ProductNotFoundException;
//import ru.practicum.mapper.CartMapper;
//import ru.practicum.repository.CartRepository;
//import ru.practicum.repository.ProductCartRepository;
//import ru.practicum.repository.ProductRepository;
//import ru.practicum.response.CartResponse;
//import ru.practicum.service.impl.CartServiceImpl;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CartServiceTest {
//
//    @Mock
//    private CartRepository cartRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ProductCartRepository productCartRepository;
//
//    @Mock
//    private CartMapper cartMapper;
//
//    @InjectMocks
//    private CartServiceImpl cartService;
//
//    private Cart cart;
//    private Product product;
//    private ProductCart productCart;
//    private CartResponse cartResponse;
//
//    @BeforeEach
//    void setUp() {
//        cart = Cart.builder()
//                .cartId(1L)
//                .createdOn(Timestamp.valueOf(LocalDateTime.now()))
//                .build();
//
//        product = Product.builder()
//                .id(1L)
//                .name("Test Product")
//                .description("Test Description")
//                .price(100.0)
//                .build();
//
//        productCart = ProductCart.builder()
//                .id(new ProductCartId(1L, 1L))
//                .cart(cart)
//                .product(product)
//                .quantity(1)
//                .build();
//
//        cartResponse = new CartResponse();
//        cartResponse.setCartId(1L);
//    }
//
//    @Test
//    void getCart_ShouldReturnCartResponse() throws CartNotFoundException {
//        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//        when(cartMapper.toDto(cart)).thenReturn(cartResponse);
//
//        CartResponse result = cartService.getCart(1L);
//
//        assertNotNull(result);
//        assertEquals(cartResponse, result);
//
//        verify(cartRepository, times(1)).findById(1L);
//        verify(cartMapper, times(1)).toDto(cart);
//    }
//
//    @Test
//    void getCart_ShouldThrowCartNotFoundException() {
//        when(cartRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(CartNotFoundException.class, () -> cartService.getCart(1L));
//
//        verify(cartRepository, times(1)).findById(1L);
//        verify(cartMapper, never()).toDto(any());
//    }
//
//    @Test
//    void addProductToCart_ShouldReturnCartResponse() throws ProductNotFoundException {
//        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(productCartRepository.findById(any(ProductCartId.class))).thenReturn(Optional.of(productCart));
//        when(cartMapper.toDto(cart)).thenReturn(cartResponse);
//
//        CartResponse result = cartService.addProductToCart(1L, 1L, 1);
//
//        assertNotNull(result);
//        assertEquals(cartResponse, result);
//
//        verify(cartRepository, times(1)).findById(1L);
//        verify(productRepository, times(1)).findById(1L);
//        verify(productCartRepository, times(1)).findById(any(ProductCartId.class));
//        verify(cartMapper, times(1)).toDto(cart);
//    }
//
//    @Test
//    void addProductToCart_ShouldThrowProductNotFoundException() {
//        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () -> cartService.addProductToCart(1L, 1L, 1));
//
//        verify(cartRepository, times(1)).findById(1L);
//        verify(productRepository, times(1)).findById(1L);
//        verify(productCartRepository, never()).findById(any(ProductCartId.class));
//        verify(cartMapper, never()).toDto(any());
//    }
//
//    @Test
//    void deleteProductFromCart_ShouldReturnCartResponse() throws CartNotFoundException {
//        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
//        when(productCartRepository.findById(any(ProductCartId.class))).thenReturn(Optional.of(productCart));
//        when(cartMapper.toDto(cart)).thenReturn(cartResponse);
//
//        CartResponse result = cartService.deleteProductFromCart(1L, 1L);
//
//        assertNotNull(result);
//        assertEquals(cartResponse, result);
//
//        verify(cartRepository, times(1)).findById(1L);
//        verify(productCartRepository, times(1)).findById(any(ProductCartId.class));
//        verify(cartMapper, times(1)).toDto(cart);
//    }
//
//    @Test
//    void deleteProductFromCart_ShouldThrowCartNotFoundException() {
//        when(cartRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(CartNotFoundException.class, () -> cartService.deleteProductFromCart(1L, 1L));
//
//        verify(cartRepository, times(1)).findById(1L);
//        verify(productCartRepository, never()).findById(any(ProductCartId.class));
//        verify(cartMapper, never()).toDto(any());
//    }
//}