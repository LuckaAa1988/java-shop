package ru.practicum.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.practicum.entity.User;

public interface PaymentRepository extends R2dbcRepository<User, Long> {
}
