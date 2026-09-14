package com.example.lifecycle.design;

public interface PaymentAuthorizedListener {

    void onPaymentCompleted(PaymentReceipt receipt);
}
