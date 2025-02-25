package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.client.CartClient;
import ru.practicum.client.OrderClient;
import ru.practicum.response.OrderShortResponse;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderClient orderClient;
    private final CartClient cartClient;

    @GetMapping
    public String ordersPage(Model model) {
        var orders = orderClient.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("sum", orders.stream().mapToDouble(OrderShortResponse::getSum).sum());
        model.addAttribute("cartItemCount", cartClient.getCart().getProducts().size());
        return "orders";
    }

    @GetMapping("/{orderId}")
    public String orderPage(@PathVariable Long orderId,
                            Model model) {
        model.addAttribute("order", orderClient.findById(orderId));
        model.addAttribute("cartItemCount", cartClient.getCart().getProducts().size());
        return "order";
    }
}
