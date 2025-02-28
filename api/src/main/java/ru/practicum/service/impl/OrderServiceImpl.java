package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.entity.Order;
import ru.practicum.entity.ProductOrder;
import ru.practicum.entity.ProductOrderId;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.OrderNotFoundException;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.repository.CartRepository;
import ru.practicum.repository.OrderRepository;
import ru.practicum.repository.ProductOrderRepository;
import ru.practicum.response.OrderFullResponse;
import ru.practicum.response.OrderShortResponse;
import ru.practicum.service.OrderService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductOrderRepository productOrderRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderShortResponse> findAll() {
        return orderRepository.findAllWithOrderSum();
    }

    @Override
    public OrderFullResponse findById(Long orderId) throws OrderNotFoundException {
        return orderMapper.toDto(orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Заказа с id %s не существует", orderId))));
    }

    @Override
    public OrderFullResponse createOrder(Long cartId) throws CartNotFoundException {
        var cart = cartRepository.findById(cartId).orElseThrow(
                () -> new CartNotFoundException(String.format("Корзины с id %s не существует", cartId))
        );
        var order = Order.builder()
                .createdOn(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        orderRepository.saveAndFlush(order);

        List<ProductOrder> products = cart.getProducts().stream()
                .map(pc -> {
                    var po = orderMapper.toOrder(pc, order);
                    ProductOrderId productOrderId = ProductOrderId.builder()
                            .orderId(order.getOrderId())
                            .productId(pc.getId().getProductId())
                            .build();
                    po.setId(productOrderId);
                    return po;
                }).toList();

        productOrderRepository.saveAll(products);

        order.setProducts(products);

        return orderMapper.toDto(order);
    }
}
