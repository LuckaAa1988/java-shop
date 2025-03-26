package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler({CartNotFoundException.class, ProductNotFoundException.class, OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorMessage> notFoundException(Exception e) {
        return Mono.just(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorMessage> ioException(Exception e) {
        return Mono.just(new ErrorMessage(e.getMessage()));
    }
}
