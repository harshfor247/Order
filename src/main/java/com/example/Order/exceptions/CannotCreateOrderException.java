package com.example.Order.exceptions;

public class CannotCreateOrderException extends RuntimeException{
    public CannotCreateOrderException(String message) {
        super(message);
    }
}
