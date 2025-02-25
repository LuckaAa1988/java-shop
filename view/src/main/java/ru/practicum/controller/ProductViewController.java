package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.ProductClient;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductClient productClient;

    @GetMapping
    public String indexPage(@RequestParam(name = "size", defaultValue = "10") Integer size,
                            @RequestParam(name = "sort", required = false) String sort,
                            @RequestParam(name = "text", required = false) String text,
                            Model model) {
        model.addAttribute("products", productClient.findAll(size, sort, text));
        return "index";
    }

    @GetMapping("/products/{id}")
    public String findById(@PathVariable Long id,
                           Model model) {
        model.addAttribute("product", productClient.findById(id));
        return "product";
    }
}
