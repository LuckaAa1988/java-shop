package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.practicum.client.CartClient;
import ru.practicum.client.ProductClient;
import ru.practicum.response.ProductFullResponse;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductClient productClient;
    private final CartClient cartClient;

    @GetMapping
    public String indexPage(@RequestParam(name = "size", defaultValue = "10") Integer size,
                            @RequestParam(name = "sort", required = false) String sort,
                            @RequestParam(name = "text", required = false) String text,
                            Model model) {
        model.addAttribute("products", productClient.findAll(size, sort, text));
        model.addAttribute("cartItemCount", cartClient.getCart().getProducts().size());
        return "index";
    }

    @GetMapping("/products/{id}")
    public String findById(@PathVariable Long id,
                           Model model) {
        model.addAttribute("product", productClient.findById(id));
        model.addAttribute("cartItemCount", cartClient.getCart().getProducts().size());
        return "product";
    }

    @GetMapping("/products/add")
    public String addPage(Model model) {
        model.addAttribute("product", ProductFullResponse.builder().build());
        model.addAttribute("cartItemCount", cartClient.getCart().getProducts().size());
        return "add";
    }

    @PostMapping("/products")
    public String addProduct(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam Double price,
                             @RequestParam MultipartFile image,
                             Model model) {
        model.addAttribute("product", productClient.addProduct(name, description, price, image));
        return "add";
    }
}
