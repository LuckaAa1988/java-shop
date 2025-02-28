package unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.entity.*;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.repository.CartRepository;
import ru.practicum.repository.OrderRepository;
import ru.practicum.repository.ProductOrderRepository;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.service.impl.OrderServiceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private Order order;
    private OrderShortResponse orderShortResponse;
    private OrderFullResponse orderFullResponse;
    private ProductCart productCart;
    private ProductOrder productOrder;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1L);

        order = Order.builder()
                .orderId(1L)
                .createdOn(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        orderShortResponse = new OrderShortResponse();
        orderShortResponse.setId(1L);

        orderFullResponse = new OrderFullResponse();
        orderFullResponse.setOrderId(1L);

        productCart = new ProductCart();
        productCart.setId(new ProductCartId(1L, 1L));

        productOrder = new ProductOrder();
        productOrder.setId(new ProductOrderId(1L, 1L));

        cart.setProducts(List.of(productCart));
    }

    @Test
    void findAll_ShouldReturnListOfOrderShortResponses() {
        when(orderRepository.findAllWithOrderSum()).thenReturn(List.of(orderShortResponse));

        List<OrderShortResponse> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderShortResponse, result.get(0));

        verify(orderRepository, times(1)).findAllWithOrderSum();
    }

    @Test
    void findById_ShouldReturnOrderFullResponse() throws OrderNotFoundException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderFullResponse);

        OrderFullResponse result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(orderFullResponse, result);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toDto(order);
    }

    @Test
    void findById_ShouldThrowOrderNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, never()).toDto(any());
    }

    @Test
    void createOrder_ShouldReturnOrderFullResponse() throws CartNotFoundException {
        try (MockedStatic<Order> mockedOrder = mockStatic(Order.class)) {
            Order.OrderBuilder builderMock = mock(Order.OrderBuilder.class);

            when(Order.builder()).thenReturn(builderMock);
            when(builderMock.orderId(anyLong())).thenReturn(builderMock);
            when(builderMock.createdOn(any(Timestamp.class))).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(order);

            Order result = Order.builder()
                    .orderId(1L)
                    .createdOn(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

            assertSame(order, result);

            when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
            when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(order);
            when(orderMapper.toOrder(any(ProductCart.class), any(Order.class))).thenReturn(productOrder);
            when(orderMapper.toDto(order)).thenReturn(orderFullResponse);

            OrderFullResponse res = orderService.createOrder(1L);

            assertNotNull(res);
            assertEquals(orderFullResponse, res);

            verify(cartRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).saveAndFlush(any(Order.class));
            verify(productOrderRepository, times(1)).saveAll(anyList());
            verify(orderMapper, times(1)).toDto(order);
        }
    }

    @Test
    void createOrder_ShouldThrowCartNotFoundException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> orderService.createOrder(1L));

        verify(cartRepository, times(1)).findById(1L);
        verify(orderRepository, never()).saveAndFlush(any(Order.class));
        verify(productOrderRepository, never()).saveAll(anyList());
        verify(orderMapper, never()).toDto(any());
    }
}
