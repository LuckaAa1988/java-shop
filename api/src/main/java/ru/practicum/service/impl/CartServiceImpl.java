package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.entity.Cart;
import ru.practicum.entity.ProductCart;
import ru.practicum.entity.ProductCartId;
import ru.practicum.exception.CartNotFoundException;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.mapper.CartMapper;
import ru.practicum.mapper.ProductMapper;
import ru.practicum.repository.CartRepository;
import ru.practicum.repository.ProductCartRepository;
import ru.practicum.repository.ProductRepository;
import ru.practicum.response.CartResponse;
import ru.practicum.service.CartService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductCartRepository productCartRepository;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public CartResponse getCart(Long id) throws CartNotFoundException {
        return cartMapper.toDto(cartRepository.findById(id).orElseThrow(
                () -> new CartNotFoundException(String.format("Корзины с id %s не существует", id))));
    }

    @Override
    public CartResponse addProductToCart(Long cartId, Long productId, Integer quantity) throws ProductNotFoundException {
        var cart = cartRepository.findById(cartId)
                .orElse(Cart.builder()
                        .createdOn(Timestamp.valueOf(LocalDateTime.now()))
                        .build());
        var product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(String.format("Товара с id %s не существует", productId))
        );
        cartRepository.save(cart);
        ProductCartId productCartId = ProductCartId.builder()
                .cartId(cart.getCartId())
                .productId(product.getId())
                .build();
        ProductCart productCart = productCartRepository.findById(productCartId)
                .orElse(ProductCart.builder()
                        .id(productCartId)
                        .cart(cart)
                        .product(product)
                        .quantity(0)
                        .build());
        productCart.setQuantity(quantity);
        productCartRepository.save(productCart);
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponse deleteProductFromCart(Long cartId, Long productId) throws CartNotFoundException {
        var cart = cartRepository.findById(cartId).orElseThrow(
                () -> new CartNotFoundException(String.format("Корзины с id %s не существует", cartId))
        );
        var productCart = productCartRepository.findById(ProductCartId.builder()
                        .cartId(cartId)
                        .productId(productId)
                .build()).orElseThrow(
                        () -> new CartNotFoundException(String.format("Корзины с id %s не существует", cartId)));
        productCartRepository.delete(productCart);
        return cartMapper.toDto(cart);
    }

    @Override
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}
