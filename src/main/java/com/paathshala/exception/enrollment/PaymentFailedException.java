package com.paathshala.exception.enrollment;

public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message)
    {
        super(message);
    }
}
