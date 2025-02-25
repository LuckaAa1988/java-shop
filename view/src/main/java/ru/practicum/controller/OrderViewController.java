package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.client.OrderClient;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderViewController {

    private final OrderClient orderClient;

    @GetMapping
    public String ordersPage(Model model) {
        model.addAttribute("orders", orderClient.findAll());
        return "orders";
    }
}
