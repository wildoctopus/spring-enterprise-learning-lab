package com.example.lifecycle.design;

public interface FraudCheck {

    boolean isAllowed(PaymentCommand command);
}
