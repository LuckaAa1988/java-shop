package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.CartClient;


@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private final CartClient cartClient;


    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpServletRequest request) {
        cartClient.addProductToCart(productId, quantity);
        String referer = request.getHeader("Referer");
        if (referer.contains("cart") || referer.contains("products")) {
            return "redirect:/cart";
        }
        else return "redirect:/";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") Long productId,
                                 HttpServletRequest request) {
        cartClient.removeProductFromCart(productId);
        String referer = request.getHeader("Referer");
        if (referer.contains("cart") || referer.contains("products")) {
            return "redirect:/cart";
        }
        else return "redirect:/";
    }

    @GetMapping
    public String cartPage(Model model) {
        var cart = cartClient.getCart();
        model.addAttribute("cart", cart);
        model.addAttribute("sum", cart.getProducts().stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum());
        return "cart";
    }

}
