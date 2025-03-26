package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.mapper.UserMapper;
import ru.practicum.repository.PaymentRepository;
import ru.practicum.response.UserResponse;
import ru.practicum.service.PaymentService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<UserResponse> findById(Long id) {
        return paymentRepository.findById(id)
                .doOnSubscribe(subscription -> log.info("Получаем пользователя с id {}", id))
                .switchIfEmpty(Mono.error(new RuntimeException(String.format("Пользователя с id %s не существует", id))))
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .balance(user.getBalance())
                        .build())
                .doOnSuccess(response -> log.info("Пользователm по id {} успешно получен", id))
                .doOnError(error -> log.error("Ошибка при получении пользователя по id {}", id, error));
    }

    @Override
    public Mono<UserResponse> withdraw(Long id, Double amount) {
        return databaseClient.sql("UPDATE users SET balance = balance - :amount WHERE id = :id")
                .bind("id", id)
                .bind("amount", amount)
                .fetch()
                .rowsUpdated()
                .doOnSubscribe(subscription -> log.info("Списываем с баланса пользователя с id {} - {} рублей", id, amount))
                .doOnSuccess(response -> log.info("Удачно списано {} р у пользователя с id {}", amount, id))
                .doOnError(error -> log.error("Ошибка при списании у пользователя с id {}", id, error))
                .then(findById(id));
    }
}
