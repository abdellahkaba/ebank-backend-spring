package com.ebank.ebankbackend.exceptions;

public class BalanceNotSufficientException extends Exception {
    public BalanceNotSufficientException(String messge) {
        super(messge);
    }
}
