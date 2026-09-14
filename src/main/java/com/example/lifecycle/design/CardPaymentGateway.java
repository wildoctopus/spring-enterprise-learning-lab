package com.example.lifecycle.design;

/** Strategy implementation: card-specific payment behavior. */
public final class CardPaymentGateway implements PaymentGateway {

    @Override
    public PaymentReceipt charge(PaymentCommand command) {
        return new PaymentReceipt(command.orderId(), "card", "AUTHORIZED");
    }
}
