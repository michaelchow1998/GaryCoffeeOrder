package com.garycoffee.order.exception;

public class StockNotEnoughException extends RuntimeException{

    public StockNotEnoughException(String message) {
        super(message);
    }
}
