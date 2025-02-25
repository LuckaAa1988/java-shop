package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler({CartNotFoundException.class, ProductNotFoundException.class, OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundException(Exception e) {
        return new ErrorMessage(e.getMessage());
    }
}
