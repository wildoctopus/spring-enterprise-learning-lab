package com.example.lifecycle.design;

public interface PaymentGateway {

    PaymentReceipt charge(PaymentCommand command);
}
