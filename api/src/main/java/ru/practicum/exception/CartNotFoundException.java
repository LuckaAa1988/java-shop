package ru.practicum.exception;

public class CartNotFoundException extends Exception {
    public CartNotFoundException(String message) {
        super(message);
    }
}
