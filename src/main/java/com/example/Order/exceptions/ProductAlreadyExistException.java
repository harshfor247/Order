package com.example.Order.exceptions;

public class ProductAlreadyExistException extends RuntimeException{
    public ProductAlreadyExistException(String message) {
        super(message);
    }
}
