//package ru.practicum.integration;
//
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.practicum.exception.CartNotFoundException;
//import ru.practicum.exception.OrderNotFoundException;
//import ru.practicum.exception.ProductNotFoundException;
//import ru.practicum.service.CartService;
//import ru.practicum.service.OrderService;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class OrderCartIntegrationTest {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private CartService cartService;
//
//
//    @Test
//    @Transactional
//    void createOrder_ShouldReturnOrderWithId1() throws ProductNotFoundException, CartNotFoundException {
//        var cart = cartService.addProductToCart(1L, 1L, 10);
//        var order = orderService.createOrder(1L);
//
//        assertAll(
//                () -> assertNotNull(cart),
//                () -> assertNotNull(order),
//                () -> assertEquals(1, order.getOrderId())
//        );
//    }
//
//    @Test
//    @Transactional
//    void findById_ShouldReturnOrderWith1() throws OrderNotFoundException, CartNotFoundException {
//        orderService.createOrder(1L);
//
//        var result = orderService.findById(2L);
//
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(2, result.getOrderId())
//        );
//    }
//
//    @Test
//    void findById_ShouldThrowNotFoundException() {
//        OrderNotFoundException thrown = assertThrows(OrderNotFoundException.class, () -> {
//            orderService.findById(100L);
//        });
//
//        assertEquals("Заказа с id 100 не существует", thrown.getMessage());
//    }
//}
