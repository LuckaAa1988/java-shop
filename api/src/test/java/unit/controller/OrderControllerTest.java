package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.controller.OrderController;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.ExceptionApiHandler;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.service.OrderService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(ExceptionApiHandler.class)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_ShouldReturnListOfOrderShortResponses() throws Exception {
        OrderShortResponse orderShortResponse = new OrderShortResponse();
        orderShortResponse.setId(1L);

        when(orderService.findAll()).thenReturn(List.of(orderShortResponse));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(orderShortResponse))));

        verify(orderService, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnOrderFullResponse() throws Exception {
        OrderFullResponse orderFullResponse = new OrderFullResponse();
        orderFullResponse.setOrderId(1L);

        when(orderService.findById(1L)).thenReturn(orderFullResponse);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderFullResponse)));

        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowOrderNotFoundException() throws Exception {
        when(orderService.findById(1L)).thenThrow(new OrderNotFoundException("Товара с id 1 не существует"));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void createOrder_ShouldReturnOrderFullResponse() throws Exception {
        OrderFullResponse orderFullResponse = new OrderFullResponse();
        orderFullResponse.setOrderId(1L);

        when(orderService.createOrder(1L)).thenReturn(orderFullResponse);

        mockMvc.perform(post("/api/orders/cart/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderFullResponse)));

        verify(orderService, times(1)).createOrder(1L);
    }

    @Test
    void createOrder_ShouldThrowCartNotFoundException() throws Exception {
        when(orderService.createOrder(1L)).thenThrow(new CartNotFoundException("Корзины с id 1 не существует"));

        mockMvc.perform(post("/api/orders/cart/1"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).createOrder(1L);
    }
}