package ru.practicum.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.entity.Product;
import ru.practicum.util.SortOrder;

@Component
public class ProductSpecification {

    public static Specification<Product> byText(String text) {
        if (text == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return Specification.where(byName(text)).or(byDescription(text));
    }

    public static Specification<Product> orderBy(SortOrder sortOrder) {
        return (root, query, cb) -> {
            switch (sortOrder) {
                case null -> cb.conjunction();
                case SortOrder.PRICE_ASC -> query.orderBy(cb.asc(root.get("price")));
                case SortOrder.PRICE_DESC -> query.orderBy(cb.desc(root.get("price")));
                case SortOrder.ALPHABETICAL_ASC -> query.orderBy(cb.asc(root.get("name")));
                case SortOrder.ALPHABETICAL_DESC -> query.orderBy(cb.desc(root.get("name")));
            }
            return cb.conjunction();
        };
    }

    public static Specification<Product> byName(String text) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Product> byDescription(String text) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%");
    }
}
